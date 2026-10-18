package com.alea.pokemon.application.strategy;

import com.alea.pokemon.domain.model.Pokemon;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Ranking strategy that sorts Pokémon in descending order of {@link Pokemon#getWeight()}.
 */
@Component
public class HeaviestRankingStrategy implements RankingStrategy {

    @Override
    public RankingCriteria getCriteria() {
        return RankingCriteria.HEAVIEST;
    }

    @Override
    public List<Pokemon> rank(List<Pokemon> pokemons, int limit) {
        return pokemons.stream()
                .sorted(Comparator.comparingInt(Pokemon::getWeight).reversed())
                .limit(Math.max(0, limit))
                .toList();
    }
}

