package com.alea.pokemon.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Pokemon domain entity.
 */
class PokemonTest {

    @Test
    void shouldBuildPokemonWithAllAttributes() {
        Pokemon pokemon = Pokemon.builder()
                .id(6)
                .name("charizard")
                .weight(905)
                .height(17)
                .baseExperience(267)
                .build();

        assertThat(pokemon.getId()).isEqualTo(6);
        assertThat(pokemon.getName()).isEqualTo("charizard");
        assertThat(pokemon.getWeight()).isEqualTo(905);
        assertThat(pokemon.getHeight()).isEqualTo(17);
        assertThat(pokemon.getBaseExperience()).isEqualTo(267);
    }

    @Test
    void shouldConsiderEqualityById() {
        Pokemon a = Pokemon.builder().id(6).name("charizard").weight(905).height(17).baseExperience(267).build();
        Pokemon b = Pokemon.builder().id(6).name("charizard").weight(905).height(17).baseExperience(267).build();
        Pokemon c = Pokemon.builder().id(1).name("bulbasaur").weight(69).height(7).baseExperience(64).build();

        assertThat(a).isEqualTo(b);
        assertThat(a).isNotEqualTo(c);
    }

    @Test
    void shouldProvideToString() {
        Pokemon pokemon = Pokemon.builder().id(25).name("pikachu").weight(60).height(4).baseExperience(112).build();
        assertThat(pokemon.toString()).contains("pikachu");
    }
}

