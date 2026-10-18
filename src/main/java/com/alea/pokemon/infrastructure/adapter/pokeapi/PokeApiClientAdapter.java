package com.alea.pokemon.infrastructure.adapter.pokeapi;

import com.alea.pokemon.domain.exception.PokemonServiceUnavailableException;
import com.alea.pokemon.domain.model.Pokemon;
import com.alea.pokemon.domain.port.PokemonPort;
import com.alea.pokemon.infrastructure.adapter.pokeapi.dto.PokeApiNamedResource;
import com.alea.pokemon.infrastructure.adapter.pokeapi.dto.PokeApiNamedResourceListResponse;
import com.alea.pokemon.infrastructure.adapter.pokeapi.dto.PokeApiPokemonResponse;
import com.alea.pokemon.infrastructure.config.PokeApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Infrastructure adapter implementing {@link PokemonPort} using Spring {@link RestClient}.
 * Fetches Pokémon data from the public PokéAPI and maps it to domain entities.
 *
 * <p>Implements the <em>Adapter Pattern</em> — the domain remains isolated from
 * HTTP/JSON framework concerns, which are fully contained in this class.</p>
 *
 * <p>Results are cached via {@code @Cacheable} to avoid redundant network calls
 * and prevent PokéAPI rate-limiting.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PokeApiClientAdapter implements PokemonPort {

    private static final String POKEMON_LIST_PATH = "/pokemon";
    private static final String POKEMON_DETAIL_PATH = "/pokemon/{name}";

    private final RestClient pokeApiRestClient;
    private final PokeApiProperties pokeApiProperties;

    /**
     * {@inheritDoc}
     *
     * <p>Fetches {@code limit} Pokémon names from the PokéAPI list endpoint, then
     * retrieves each Pokémon's details concurrently (sequentially for simplicity)
     * and maps them to domain {@link Pokemon} entities.</p>
     *
     * @param limit maximum number of Pokémon to retrieve
     * @return list of {@link Pokemon} domain entities
     * @throws PokemonServiceUnavailableException if the external PokéAPI is unreachable or returns an error
     */
    @Override
    @Cacheable("pokemons")
    public List<Pokemon> findAll(int limit) {
        log.info("Fetching up to {} Pokémon from PokéAPI", limit);
        try {
            PokeApiNamedResourceListResponse listResponse = pokeApiRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(POKEMON_LIST_PATH)
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .body(PokeApiNamedResourceListResponse.class);

            if (listResponse == null || listResponse.getResults() == null) {
                log.warn("PokéAPI returned null or empty list response");
                return List.of();
            }

            List<Pokemon> pokemons = new ArrayList<>();
            for (PokeApiNamedResource resource : listResponse.getResults()) {
                try {
                    PokeApiPokemonResponse detail = pokeApiRestClient.get()
                            .uri(POKEMON_DETAIL_PATH, resource.getName())
                            .retrieve()
                            .body(PokeApiPokemonResponse.class);

                    if (detail != null) {
                        pokemons.add(mapToDomain(detail));
                    }
                } catch (RestClientException e) {
                    log.warn("Failed to fetch details for Pokémon '{}': {}", resource.getName(), e.getMessage());
                }
            }

            log.info("Successfully fetched {} Pokémon from PokéAPI", pokemons.size());
            return pokemons;

        } catch (RestClientException e) {
            log.error("Failed to fetch Pokémon list from PokéAPI: {}", e.getMessage(), e);
            throw new PokemonServiceUnavailableException(
                    "Failed to fetch Pokémon data from upstream PokéAPI service", e);
        }
    }

    /**
     * Maps a {@link PokeApiPokemonResponse} external DTO to a {@link Pokemon} domain entity.
     *
     * @param response the PokéAPI detail response
     * @return domain entity
     */
    private Pokemon mapToDomain(PokeApiPokemonResponse response) {
        return Pokemon.builder()
                .id(response.getId())
                .name(response.getName())
                .weight(response.getWeight())
                .height(response.getHeight())
                .baseExperience(response.getBaseExperience())
                .build();
    }
}

