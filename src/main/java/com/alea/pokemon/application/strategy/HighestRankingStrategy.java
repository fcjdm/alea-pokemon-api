package com.alea.pokemon.application.strategy;

import com.alea.pokemon.domain.model.Pokemon;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Ranking strategy that sorts Pokémon in descending order of {@link Pokemon#getHeight()}.
 */
@Component
public class HighestRankingStrategy implements RankingStrategy {

    @Override
    public RankingCriteria getCriteria() {
        return RankingCriteria.HIGHEST;
    }

    @Override
    public List<Pokemon> rank(List<Pokemon> pokemons, int limit) {
        return pokemons.stream()
                .sorted(Comparator.comparingInt(Pokemon::getHeight).reversed())
                .limit(Math.max(0, limit))
                .toList();
    }
}

