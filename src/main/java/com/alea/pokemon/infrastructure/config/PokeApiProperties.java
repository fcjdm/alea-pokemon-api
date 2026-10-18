package com.alea.pokemon.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the PokéAPI HTTP client.
 * Bound from the {@code pokeapi.*} namespace in application.yaml.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pokeapi")
public class PokeApiProperties {

    /** Base URL for the PokéAPI, e.g. https://pokeapi.co/api/v2 */
    private String baseUrl = "https://pokeapi.co/api/v2";

    /** HTTP connection/read timeout in seconds. */
    private int timeoutSeconds = 10;

    /** Number of Pokémon to fetch per list request (covers all ~1300+). */
    private int fetchLimit = 100;
}

