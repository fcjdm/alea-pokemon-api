package com.alea.pokemon.infrastructure.config;

import com.alea.pokemon.infrastructure.adapter.pokeapi.PokeApiClientAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
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
 * Integration test verifying {@link PokeApiProperties} binding from application.yaml.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PokeApiPropertiesIntegrationTest {

    @Autowired
    private PokeApiProperties pokeApiProperties;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldLoadPokeApiPropertiesFromApplicationYaml() {
        assertThat(pokeApiProperties).isNotNull();
        assertThat(pokeApiProperties.getBaseUrl()).isNotBlank();
        assertThat(pokeApiProperties.getTimeoutSeconds()).isGreaterThan(0);
        assertThat(pokeApiProperties.getFetchLimit()).isGreaterThan(0);
    }

    @Test
    void cacheManagerShouldHavePokemonsCache() {
        assertThat(cacheManager.getCache("pokemons")).isNotNull();
    }
}

