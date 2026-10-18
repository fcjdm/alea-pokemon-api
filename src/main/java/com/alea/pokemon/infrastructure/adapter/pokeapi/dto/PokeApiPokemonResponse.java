package com.alea.pokemon.infrastructure.adapter.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * External DTO representing a single Pokémon's detailed data from PokéAPI.
 * Maps the relevant fields from GET /pokemon/{id}.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokeApiPokemonResponse {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("weight")
    private int weight;

    @JsonProperty("height")
    private int height;

    @JsonProperty("base_experience")
    private int baseExperience;
}

