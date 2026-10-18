package com.alea.pokemon.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Core domain entity representing a Pokémon with its statistical attributes.
 * This is a pure domain object with zero external framework dependencies.
 */
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Pokemon {

    /** Numerical Pokémon identifier from PokéAPI. */
    private final int id;

    /** Canonical lowercase Pokémon name. */
    private final String name;

    /** Weight in hectograms (as returned by PokéAPI). */
    private final int weight;

    /** Height in decimeters (as returned by PokéAPI). */
    private final int height;

    /** Base experience points awarded when defeating this Pokémon. */
    private final int baseExperience;
}

