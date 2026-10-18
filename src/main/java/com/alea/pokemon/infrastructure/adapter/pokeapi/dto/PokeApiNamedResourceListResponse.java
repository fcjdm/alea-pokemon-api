package com.alea.pokemon.infrastructure.adapter.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * External DTO for paginated named resource list responses from PokéAPI.
 * Example: GET /pokemon?limit=100 returns this structure.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokeApiNamedResourceListResponse {

    @JsonProperty("count")
    private int count;

    @JsonProperty("next")
    private String next;

    @JsonProperty("previous")
    private String previous;

    @JsonProperty("results")
    private List<PokeApiNamedResource> results;
}

