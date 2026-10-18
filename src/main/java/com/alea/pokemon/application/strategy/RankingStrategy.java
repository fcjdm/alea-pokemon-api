package com.alea.pokemon.application.strategy;

import com.alea.pokemon.domain.model.Pokemon;

import java.util.List;

/**
 * Strategy interface for ranking Pokémon by a specific attribute.
 *
 * <p>Implements the <em>Strategy Pattern</em> to make ranking criteria pluggable and extensible
 * without modifying existing services (Open-Closed Principle).</p>
 */
public interface RankingStrategy {

    /**
     * Returns the ranking criterion this strategy implements.
     *
     * @return the {@link RankingCriteria} enum value
     */
    RankingCriteria getCriteria();

    /**
     * Ranks the given list of Pokémon by this strategy's criterion and returns the top {@code limit} results.
     *
     * @param pokemons the full list of Pokémon to rank
     * @param limit    the maximum number of results to return
     * @return sorted list of at most {@code limit} Pokémon in descending order of the criterion
     */
    List<Pokemon> rank(List<Pokemon> pokemons, int limit);
}

