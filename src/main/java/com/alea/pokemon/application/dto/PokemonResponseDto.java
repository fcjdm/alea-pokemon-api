package com.alea.pokemon.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Application-layer DTO for a Pokémon returned in ranking responses.
 * Decouples the domain entity from the presentation layer.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokemonResponseDto {

    private int id;
    private String name;
    private int weight;
    private int height;
    private int baseExperience;
}

