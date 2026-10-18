package com.alea.pokemon.domain.exception;

/**
 * Thrown when the external Pokémon data service (PokéAPI) is unavailable or returns an error.
 * Maps to HTTP 502/503 in the presentation layer.
 */
public class PokemonServiceUnavailableException extends RuntimeException {

    public PokemonServiceUnavailableException(String message) {
        super(message);
    }

    public PokemonServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

