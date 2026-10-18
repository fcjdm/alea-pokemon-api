package com.alea.pokemon.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Cache configuration for the Pokémon API.
 *
 * <p>Enables in-memory caching with {@link ConcurrentMapCacheManager} to avoid redundant
 * outbound calls to PokéAPI and prevent third-party rate-limiting.</p>
 *
 * <p>Cache names:</p>
 * <ul>
 *   <li>{@code pokemons} — used by {@link com.alea.pokemon.infrastructure.adapter.pokeapi.PokeApiClientAdapter}</li>
 * </ul>
 *
 * <p>Future enhancement: replace with Redis-backed {@code RedisCacheManager} for distributed caching.</p>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /** Cache name for the Pokémon list retrieved from PokéAPI. */
    public static final String POKEMONS_CACHE = "pokemons";

    /**
     * Provides an in-memory {@link CacheManager} backed by {@link ConcurrentMapCacheManager}.
     * Only created if no other {@link CacheManager} bean is present (test-friendly).
     *
     * @return configured {@link CacheManager}
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(POKEMONS_CACHE);
    }
}

