package com.alea.pokemon.presentation.mapper;

import com.alea.pokemon.application.dto.PokemonResponseDto;
import com.alea.pokemon.presentation.dto.PokemonResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PokemonResponseMapperTest {

    private final PokemonResponseMapper mapper = new PokemonResponseMapper();

    @Test
    void shouldMapAllPokemonFieldsToPresentationResponse() {
        PokemonResponseDto dto = PokemonResponseDto.builder()
                .id(6)
                .name("charizard")
                .weight(905)
                .height(17)
                .baseExperience(267)
                .build();

        List<PokemonResponse> result = mapper.mapToPresentation(List.of(dto));

        assertThat(result).singleElement().satisfies(response -> {
            assertThat(response.getId()).isEqualTo(6);
            assertThat(response.getName()).isEqualTo("charizard");
            assertThat(response.getWeight()).isEqualTo(905);
            assertThat(response.getHeight()).isEqualTo(17);
            assertThat(response.getBaseExperience()).isEqualTo(267);
        });
    }

    @Test
    void shouldReturnEmptyListForEmptyInput() {
        assertThat(mapper.mapToPresentation(List.of())).isEmpty();
    }
}
