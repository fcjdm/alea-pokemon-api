package com.alea.pokemon.application.service;

import com.alea.pokemon.application.dto.PokemonResponseDto;
import com.alea.pokemon.application.strategy.RankingCriteria;
import com.alea.pokemon.application.strategy.RankingStrategy;
import com.alea.pokemon.domain.model.Pokemon;
import com.alea.pokemon.domain.port.PokemonPort;
import com.alea.pokemon.infrastructure.config.PokeApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Application service orchestrating Pokémon ranking use cases.
 *
 * <p>Delegates data fetching to {@link PokemonPort} and ranking logic to the appropriate
 * {@link RankingStrategy}, then maps results to {@link PokemonResponseDto}.</p>
 *
 * <p>Strategies are injected as a list by Spring and indexed by their {@link RankingCriteria}
 * for O(1) lookup.</p>
 */
@Slf4j
@Service
public class PokemonRankingService {

    private static final int TOP_LIMIT = 5;

    private final PokemonPort pokemonPort;
    private final Map<RankingCriteria, RankingStrategy> strategyMap;
    private final int fetchLimit;

    /**
     * Constructs the service, building an immutable strategy registry from all injected strategies.
     *
     * @param pokemonPort     outbound port for fetching Pokémon data
     * @param strategies      all implementations of {@link RankingStrategy} registered as Spring beans
     * @param pokeApiProperties configuration properties (provides the fetch limit)
     */
    public PokemonRankingService(PokemonPort pokemonPort,
                                 List<RankingStrategy> strategies,
                                 PokeApiProperties pokeApiProperties) {
        this.pokemonPort = pokemonPort;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(RankingStrategy::getCriteria, Function.identity()));
        this.fetchLimit = pokeApiProperties.getFetchLimit();
        log.info("PokemonRankingService initialized with strategies: {} and fetchLimit: {}",
                this.strategyMap.keySet(), this.fetchLimit);
    }

    /**
     * Returns the top 5 heaviest Pokémon.
     *
     * @return list of up to 5 {@link PokemonResponseDto} sorted by weight descending
     */
    public List<PokemonResponseDto> getHeaviest() {
        return getRanking(RankingCriteria.HEAVIEST);
    }

    /**
     * Returns the top 5 highest Pokémon.
     *
     * @return list of up to 5 {@link PokemonResponseDto} sorted by height descending
     */
    public List<PokemonResponseDto> getHighest() {
        return getRanking(RankingCriteria.HIGHEST);
    }

    /**
     * Returns the top 5 Pokémon with highest base experience.
     *
     * @return list of up to 5 {@link PokemonResponseDto} sorted by base experience descending
     */
    public List<PokemonResponseDto> getTopExperience() {
        return getRanking(RankingCriteria.EXPERIENCE);
    }

    private List<PokemonResponseDto> getRanking(RankingCriteria criteria) {
        RankingStrategy strategy = strategyMap.get(criteria);
        List<Pokemon> pokemons = pokemonPort.findAll(fetchLimit);
        return strategy.rank(pokemons, TOP_LIMIT).stream()
                .map(this::toDto)
                .toList();
    }

    private PokemonResponseDto toDto(Pokemon pokemon) {
        return PokemonResponseDto.builder()
                .id(pokemon.getId())
                .name(pokemon.getName())
                .weight(pokemon.getWeight())
                .height(pokemon.getHeight())
                .baseExperience(pokemon.getBaseExperience())
                .build();
    }
}

