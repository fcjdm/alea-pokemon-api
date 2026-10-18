package com.alea.pokemon.presentation.controller;

import com.alea.pokemon.application.dto.PokemonResponseDto;
import com.alea.pokemon.application.service.PokemonRankingService;
import com.alea.pokemon.domain.exception.PokemonServiceUnavailableException;
import com.alea.pokemon.presentation.filter.RequestLoggingFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc integration tests for {@link PokemonController}.
 * Uses {@code @WebMvcTest} to test only the web layer in isolation.
 */
@WebMvcTest(PokemonController.class)
@Import(RequestLoggingFilter.class)
class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PokemonRankingService pokemonRankingService;

    private static final List<PokemonResponseDto> SAMPLE_POKEMONS = List.of(
            PokemonResponseDto.builder().id(143).name("snorlax").weight(4600).height(21).baseExperience(189).build(),
            PokemonResponseDto.builder().id(321).name("wailord").weight(3980).height(145).baseExperience(175).build(),
            PokemonResponseDto.builder().id(6).name("charizard").weight(905).height(17).baseExperience(267).build(),
            PokemonResponseDto.builder().id(89).name("muk").weight(300).height(12).baseExperience(175).build(),
            PokemonResponseDto.builder().id(788).name("tapu-fini").weight(212).height(13).baseExperience(175).build()
    );

    // --- GET /api/v1/pokemon/heaviest ---

    @Test
    void getHeaviestShouldReturn200WithPokemonList() throws Exception {
        when(pokemonRankingService.getHeaviest()).thenReturn(SAMPLE_POKEMONS);

        mockMvc.perform(get("/api/v1/pokemon/heaviest").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-Id"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("snorlax"))
                .andExpect(jsonPath("$[0].weight").value(4600))
                .andExpect(jsonPath("$[0].id").value(143));
    }

    @Test
    void getHeaviestShouldReturn200WithEmptyArrayWhenNoPokemons() throws Exception {
        when(pokemonRankingService.getHeaviest()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pokemon/heaviest").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/v1/pokemon/highest ---

    @Test
    void getHighestShouldReturn200WithPokemonList() throws Exception {
        when(pokemonRankingService.getHighest()).thenReturn(SAMPLE_POKEMONS);

        mockMvc.perform(get("/api/v1/pokemon/highest").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[1].name").value("wailord"));
    }

    @Test
    void getHighestShouldReturn200WithEmptyArrayWhenNoPokemons() throws Exception {
        when(pokemonRankingService.getHighest()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pokemon/highest").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/v1/pokemon/experience ---

    @Test
    void getExperienceShouldReturn200WithPokemonList() throws Exception {
        when(pokemonRankingService.getTopExperience()).thenReturn(SAMPLE_POKEMONS);

        mockMvc.perform(get("/api/v1/pokemon/experience").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].baseExperience").value(189));
    }

    @Test
    void getExperienceShouldReturn200WithEmptyArrayWhenNoPokemons() throws Exception {
        when(pokemonRankingService.getTopExperience()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pokemon/experience").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- Error handling via GlobalExceptionHandler ---

    @Test
    void getHeaviestShouldReturn502WhenServiceUnavailable() throws Exception {
        when(pokemonRankingService.getHeaviest())
                .thenThrow(new PokemonServiceUnavailableException("PokéAPI is unreachable"));

        mockMvc.perform(get("/api/v1/pokemon/heaviest").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void getHighestShouldReturn502WhenServiceUnavailable() throws Exception {
        when(pokemonRankingService.getHighest())
                .thenThrow(new PokemonServiceUnavailableException("Upstream failure"));

        mockMvc.perform(get("/api/v1/pokemon/highest").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway())
                .andExpect(content().contentType("application/problem+json"));
    }

    @Test
    void getExperienceShouldReturn502WhenServiceUnavailable() throws Exception {
        when(pokemonRankingService.getTopExperience())
                .thenThrow(new PokemonServiceUnavailableException("Upstream failure"));

        mockMvc.perform(get("/api/v1/pokemon/experience").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway())
                .andExpect(content().contentType("application/problem+json"));
    }

    @Test
    void getHeaviestShouldReturn500OnUnexpectedError() throws Exception {
        when(pokemonRankingService.getHeaviest())
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/v1/pokemon/heaviest").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(500));
    }
}

