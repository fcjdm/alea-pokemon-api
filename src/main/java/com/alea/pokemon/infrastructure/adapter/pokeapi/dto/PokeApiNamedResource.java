package com.alea.pokemon.infrastructure.adapter.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * External DTO for a named resource reference returned by PokéAPI list endpoints.
 * Example: {"name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/"}
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokeApiNamedResource {

    @JsonProperty("name")
    private String name;

    @JsonProperty("url")
    private String url;
}

