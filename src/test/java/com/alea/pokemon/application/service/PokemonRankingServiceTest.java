package com.alea.pokemon.application.service;

import com.alea.pokemon.application.dto.PokemonResponseDto;
import com.alea.pokemon.application.strategy.ExperienceRankingStrategy;
import com.alea.pokemon.application.strategy.HeaviestRankingStrategy;
import com.alea.pokemon.application.strategy.HighestRankingStrategy;
import com.alea.pokemon.application.strategy.RankingStrategy;
import com.alea.pokemon.domain.exception.PokemonServiceUnavailableException;
import com.alea.pokemon.domain.model.Pokemon;
import com.alea.pokemon.domain.port.PokemonPort;
import com.alea.pokemon.infrastructure.config.PokeApiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Mockito unit tests for {@link PokemonRankingService}.
 * Verifies orchestration logic, DTO mapping, and exception propagation.
 */
@ExtendWith(MockitoExtension.class)
class PokemonRankingServiceTest {

    @Mock
    private PokemonPort pokemonPort;

    private PokemonRankingService service;

    private List<Pokemon> samplePokemons;

    @BeforeEach
    void setUp() {
        samplePokemons = List.of(
                Pokemon.builder().id(1).name("bulbasaur").weight(69).height(7).baseExperience(64).build(),
                Pokemon.builder().id(6).name("charizard").weight(905).height(17).baseExperience(267).build(),
                Pokemon.builder().id(143).name("snorlax").weight(4600).height(21).baseExperience(189).build(),
                Pokemon.builder().id(89).name("muk").weight(300).height(12).baseExperience(175).build(),
                Pokemon.builder().id(321).name("wailord").weight(3980).height(145).baseExperience(175).build(),
                Pokemon.builder().id(788).name("tapu-fini").weight(212).height(13).baseExperience(175).build()
        );

        PokeApiProperties props = new PokeApiProperties();
        props.setFetchLimit(100);

        List<RankingStrategy> strategies = List.of(
                new HeaviestRankingStrategy(),
                new HighestRankingStrategy(),
                new ExperienceRankingStrategy()
        );

        service = new PokemonRankingService(pokemonPort, strategies, props);
    }

    @Test
    void getHeaviestShouldReturnTop5ByWeight() {
        when(pokemonPort.findAll(anyInt())).thenReturn(samplePokemons);

        List<PokemonResponseDto> result = service.getHeaviest();

        assertThat(result).hasSize(5);
        assertThat(result.get(0).getName()).isEqualTo("snorlax");   // 4600
        assertThat(result.get(1).getName()).isEqualTo("wailord");   // 3980
        assertThat(result.get(2).getName()).isEqualTo("charizard"); // 905

        verify(pokemonPort).findAll(anyInt());
    }

    @Test
    void getHighestShouldReturnTop5ByHeight() {
        when(pokemonPort.findAll(anyInt())).thenReturn(samplePokemons);

        List<PokemonResponseDto> result = service.getHighest();

        assertThat(result).hasSize(5);
        assertThat(result.get(0).getName()).isEqualTo("wailord");   // 145
        assertThat(result.get(1).getName()).isEqualTo("snorlax");   // 21
        assertThat(result.get(2).getName()).isEqualTo("charizard"); // 17

        verify(pokemonPort).findAll(anyInt());
    }

    @Test
    void getTopExperienceShouldReturnTop5ByBaseExperience() {
        when(pokemonPort.findAll(anyInt())).thenReturn(samplePokemons);

        List<PokemonResponseDto> result = service.getTopExperience();

        assertThat(result).hasSize(5);
        assertThat(result.get(0).getName()).isEqualTo("charizard"); // 267
        assertThat(result.get(1).getName()).isEqualTo("snorlax");   // 189

        verify(pokemonPort).findAll(anyInt());
    }

    @Test
    void getHeaviestShouldMapAllDtoFieldsCorrectly() {
        when(pokemonPort.findAll(anyInt())).thenReturn(List.of(
                Pokemon.builder().id(6).name("charizard").weight(905).height(17).baseExperience(267).build()
        ));

        List<PokemonResponseDto> result = service.getHeaviest();

        assertThat(result).hasSize(1);
        PokemonResponseDto dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(6);
        assertThat(dto.getName()).isEqualTo("charizard");
        assertThat(dto.getWeight()).isEqualTo(905);
        assertThat(dto.getHeight()).isEqualTo(17);
        assertThat(dto.getBaseExperience()).isEqualTo(267);
    }

    @Test
    void getHeaviestShouldReturnEmptyListWhenPortReturnsEmpty() {
        when(pokemonPort.findAll(anyInt())).thenReturn(List.of());

        List<PokemonResponseDto> result = service.getHeaviest();

        assertThat(result).isEmpty();
    }

    @Test
    void getHeaviestShouldPropagateServiceUnavailableException() {
        when(pokemonPort.findAll(anyInt()))
                .thenThrow(new PokemonServiceUnavailableException("PokéAPI is down"));

        assertThatThrownBy(() -> service.getHeaviest())
                .isInstanceOf(PokemonServiceUnavailableException.class)
                .hasMessageContaining("PokéAPI is down");
    }
}

