package com.alea.pokemon.application.strategy;

import com.alea.pokemon.domain.model.Pokemon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for all {@link RankingStrategy} implementations.
 * Verifies sorting, limit bounds, and criteria identity.
 */
class RankingStrategyTest {

    private List<Pokemon> pokemons;

    @BeforeEach
    void setUp() {
        pokemons = List.of(
                Pokemon.builder().id(1).name("bulbasaur").weight(69).height(7).baseExperience(64).build(),
                Pokemon.builder().id(6).name("charizard").weight(905).height(17).baseExperience(267).build(),
                Pokemon.builder().id(143).name("snorlax").weight(4600).height(21).baseExperience(189).build(),
                Pokemon.builder().id(89).name("muk").weight(300).height(12).baseExperience(175).build(),
                Pokemon.builder().id(321).name("wailord").weight(3980).height(145).baseExperience(175).build(),
                Pokemon.builder().id(788).name("tapu-fini").weight(212).height(13).baseExperience(175).build()
        );
    }

    // --- HeaviestRankingStrategy ---

    @Test
    void heaviestStrategyShouldReturnCriteriaHeaviest() {
        assertThat(new HeaviestRankingStrategy().getCriteria()).isEqualTo(RankingCriteria.HEAVIEST);
    }

    @Test
    void heaviestStrategyShouldSortByWeightDescending() {
        HeaviestRankingStrategy strategy = new HeaviestRankingStrategy();
        List<Pokemon> result = strategy.rank(pokemons, 3);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("snorlax");
        assertThat(result.get(1).getName()).isEqualTo("wailord");
        assertThat(result.get(2).getName()).isEqualTo("charizard");
    }

    @Test
    void heaviestStrategyShouldRespectLimit() {
        HeaviestRankingStrategy strategy = new HeaviestRankingStrategy();
        List<Pokemon> result = strategy.rank(pokemons, 1);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("snorlax");
    }

    @Test
    void heaviestStrategyShouldHandleEmptyList() {
        HeaviestRankingStrategy strategy = new HeaviestRankingStrategy();
        assertThat(strategy.rank(List.of(), 5)).isEmpty();
    }

    @Test
    void heaviestStrategyShouldHandleZeroLimit() {
        HeaviestRankingStrategy strategy = new HeaviestRankingStrategy();
        assertThat(strategy.rank(pokemons, 0)).isEmpty();
    }

    // --- HighestRankingStrategy ---

    @Test
    void highestStrategyShouldReturnCriteriaHighest() {
        assertThat(new HighestRankingStrategy().getCriteria()).isEqualTo(RankingCriteria.HIGHEST);
    }

    @Test
    void highestStrategyShouldSortByHeightDescending() {
        HighestRankingStrategy strategy = new HighestRankingStrategy();
        List<Pokemon> result = strategy.rank(pokemons, 3);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("wailord");   // height 145
        assertThat(result.get(1).getName()).isEqualTo("snorlax");   // height 21
        assertThat(result.get(2).getName()).isEqualTo("charizard"); // height 17
    }

    @Test
    void highestStrategyShouldRespectLimit() {
        HighestRankingStrategy strategy = new HighestRankingStrategy();
        List<Pokemon> result = strategy.rank(pokemons, 2);
        assertThat(result).hasSize(2);
    }

    @Test
    void highestStrategyShouldHandleEmptyList() {
        HighestRankingStrategy strategy = new HighestRankingStrategy();
        assertThat(strategy.rank(List.of(), 5)).isEmpty();
    }

    // --- ExperienceRankingStrategy ---

    @Test
    void experienceStrategyShouldReturnCriteriaExperience() {
        assertThat(new ExperienceRankingStrategy().getCriteria()).isEqualTo(RankingCriteria.EXPERIENCE);
    }

    @Test
    void experienceStrategyShouldSortByBaseExperienceDescending() {
        ExperienceRankingStrategy strategy = new ExperienceRankingStrategy();
        List<Pokemon> result = strategy.rank(pokemons, 3);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("charizard");   // 267
        assertThat(result.get(1).getName()).isEqualTo("snorlax");    // 189
    }

    @Test
    void experienceStrategyShouldRespectLimit() {
        ExperienceRankingStrategy strategy = new ExperienceRankingStrategy();
        List<Pokemon> result = strategy.rank(pokemons, 1);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("charizard");
    }

    @Test
    void experienceStrategyShouldHandleEmptyList() {
        ExperienceRankingStrategy strategy = new ExperienceRankingStrategy();
        assertThat(strategy.rank(List.of(), 5)).isEmpty();
    }

    @Test
    void experienceStrategyShouldHandleZeroLimit() {
        ExperienceRankingStrategy strategy = new ExperienceRankingStrategy();
        assertThat(strategy.rank(pokemons, 0)).isEmpty();
    }
}

