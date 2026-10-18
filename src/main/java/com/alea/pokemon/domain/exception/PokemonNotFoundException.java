package com.alea.pokemon.domain.exception;

/**
 * Thrown when a requested Pokémon is not found in the external data source.
 */
public class PokemonNotFoundException extends RuntimeException {

    private final int pokemonId;

    public PokemonNotFoundException(int pokemonId) {
        super("Pokémon with id " + pokemonId + " was not found.");
        this.pokemonId = pokemonId;
    }

    public PokemonNotFoundException(String name) {
        super("Pokémon with name '" + name + "' was not found.");
        this.pokemonId = -1;
    }

    public int getPokemonId() {
        return pokemonId;
    }
}

