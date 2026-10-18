package com.alea.pokemon.presentation.controller;

import com.alea.pokemon.application.dto.PokemonResponseDto;
import com.alea.pokemon.application.service.PokemonRankingService;
import com.alea.pokemon.application.strategy.RankingCriteria;
import com.alea.pokemon.domain.exception.PokemonServiceUnavailableException;
import com.alea.pokemon.presentation.converter.RankingSortByConverter;
import com.alea.pokemon.presentation.filter.RequestLoggingFilter;
import com.alea.pokemon.presentation.mapper.PokemonResponseMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc integration tests for {@link PokemonController}.
 * Uses {@code @WebMvcTest} to test only the web layer in isolation.
 */
@WebMvcTest(PokemonController.class)
@Import({RequestLoggingFilter.class, PokemonResponseMapper.class, RankingSortByConverter.class})
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

    @Test
    void getRankingByWeightShouldReturnPokemonList() throws Exception {
        when(pokemonRankingService.getRanking(RankingCriteria.HEAVIEST)).thenReturn(SAMPLE_POKEMONS);

        mockMvc.perform(get("/api/v1/pokemon").param("sortBy", "weight").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-Id"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("snorlax"))
                .andExpect(jsonPath("$[0].weight").value(4600))
                .andExpect(jsonPath("$[0].id").value(143));
    }

    @Test
    void getRankingByHeightShouldUseHeightCriterion() throws Exception {
        when(pokemonRankingService.getRanking(RankingCriteria.HIGHEST)).thenReturn(SAMPLE_POKEMONS);

        mockMvc.perform(get("/api/v1/pokemon").param("sortBy", "height").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[1].name").value("wailord"));
    }

    @Test
    void getRankingByExperienceShouldUseExperienceCriterion() throws Exception {
        when(pokemonRankingService.getRanking(RankingCriteria.EXPERIENCE)).thenReturn(SAMPLE_POKEMONS);

        mockMvc.perform(get("/api/v1/pokemon").param("sortBy", "experience").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].baseExperience").value(189));
    }

    @Test
    void getRankingShouldReturnEmptyArrayWhenNoPokemons() throws Exception {
        when(pokemonRankingService.getRanking(RankingCriteria.HEAVIEST)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pokemon").param("sortBy", "weight").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRankingShouldReturn400ProblemWhenSortByIsUnsupported() throws Exception {
        mockMvc.perform(get("/api/v1/pokemon").param("sortBy", "speed").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value(
                        "Invalid sortBy value 'speed'. Allowed values are: weight, height, experience."));
    }

    @Test
    void getRankingShouldReturn502WhenServiceUnavailable() throws Exception {
        when(pokemonRankingService.getRanking(RankingCriteria.HEAVIEST))
                .thenThrow(new PokemonServiceUnavailableException("PokéAPI is unreachable"));

        mockMvc.perform(get("/api/v1/pokemon").param("sortBy", "weight").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void getRankingShouldReturn500OnUnexpectedError() throws Exception {
        when(pokemonRankingService.getRanking(RankingCriteria.HEAVIEST))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/v1/pokemon").param("sortBy", "weight").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(500));
    }
}
