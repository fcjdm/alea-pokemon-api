package com.alea.pokemon.domain.port;

import com.alea.pokemon.domain.model.Pokemon;

import java.util.List;

/**
 * Domain port (outbound interface) for retrieving Pokémon data from an external source.
 * Implementations live in the infrastructure layer (Adapter Pattern).
 */
public interface PokemonPort {

    /**
     * Retrieves a list of Pokémon from the external data provider.
     *
     * @param limit maximum number of Pokémon to retrieve
     * @return list of {@link Pokemon} domain entities
     * @throws com.alea.pokemon.domain.exception.PokemonServiceUnavailableException if the external service is unreachable
     */
    List<Pokemon> findAll(int limit);
}

