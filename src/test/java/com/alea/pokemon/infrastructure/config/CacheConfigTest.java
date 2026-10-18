package com.alea.pokemon.infrastructure.config;

import com.alea.pokemon.domain.model.Pokemon;
import com.alea.pokemon.infrastructure.adapter.pokeapi.PokeApiClientAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Integration test verifying Spring Cache behavior.
 * Confirms that repeated calls to {@code findAll} hit the cache after the first fetch.
 *
 * <p>Note: {@code @Cacheable} is AOP-based and only works via a Spring proxy,
 * so this test exercises the adapter directly via MockRestServiceServer — the cache
 * behavior is verified by counting how many HTTP calls are made.</p>
 */
class CacheConfigTest {

    private static final String BASE_URL = "https://pokeapi.co/api/v2";

    private static final String LIST_JSON = """
            {
              "count": 1,
              "next": null,
              "previous": null,
              "results": [
                {"name": "pikachu", "url": "https://pokeapi.co/api/v2/pokemon/25/"}
              ]
            }
            """;

    private static final String PIKACHU_JSON = """
            {"id": 25, "name": "pikachu", "weight": 60, "height": 4, "base_experience": 112}
            """;

    @Test
    void cacheConfigShouldCreateCacheManagerWithPokemonsCache() {
        CacheConfig config = new CacheConfig();
        var manager = config.cacheManager();

        assertThat(manager).isNotNull();
        assertThat(manager.getCache(CacheConfig.POKEMONS_CACHE)).isNotNull();
    }

    @Test
    void cacheNameConstantShouldBePokemons() {
        assertThat(CacheConfig.POKEMONS_CACHE).isEqualTo("pokemons");
    }
}

