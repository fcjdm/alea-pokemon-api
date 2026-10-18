package com.alea.pokemon.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Spring configuration for the {@link RestClient} used to communicate with PokéAPI.
 */
@Configuration
public class RestClientConfig {

    /**
     * Creates a {@link RestClient} bean pre-configured with the PokéAPI base URL and timeouts.
     *
     * @param properties PokéAPI configuration properties
     * @return configured {@link RestClient} instance
     */
    @Bean
    public RestClient pokeApiRestClient(PokeApiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}

