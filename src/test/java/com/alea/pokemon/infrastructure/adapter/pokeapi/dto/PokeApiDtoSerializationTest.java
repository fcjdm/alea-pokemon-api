package com.alea.pokemon.infrastructure.adapter.pokeapi.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests verifying JSON serialization/deserialization of PokéAPI external DTOs.
 */
class PokeApiDtoSerializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldDeserializePokeApiPokemonResponse() throws Exception {
        String json = """
                {
                  "id": 6,
                  "name": "charizard",
                  "weight": 905,
                  "height": 17,
                  "base_experience": 267
                }
                """;

        PokeApiPokemonResponse response = objectMapper.readValue(json, PokeApiPokemonResponse.class);

        assertThat(response.getId()).isEqualTo(6);
        assertThat(response.getName()).isEqualTo("charizard");
        assertThat(response.getWeight()).isEqualTo(905);
        assertThat(response.getHeight()).isEqualTo(17);
        assertThat(response.getBaseExperience()).isEqualTo(267);
    }

    @Test
    void shouldDeserializePokeApiNamedResource() throws Exception {
        String json = """
                {
                  "name": "bulbasaur",
                  "url": "https://pokeapi.co/api/v2/pokemon/1/"
                }
                """;

        PokeApiNamedResource resource = objectMapper.readValue(json, PokeApiNamedResource.class);

        assertThat(resource.getName()).isEqualTo("bulbasaur");
        assertThat(resource.getUrl()).isEqualTo("https://pokeapi.co/api/v2/pokemon/1/");
    }

    @Test
    void shouldDeserializePokeApiNamedResourceListResponse() throws Exception {
        String json = """
                {
                  "count": 1302,
                  "next": "https://pokeapi.co/api/v2/pokemon?offset=100&limit=100",
                  "previous": null,
                  "results": [
                    {"name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/"},
                    {"name": "ivysaur",   "url": "https://pokeapi.co/api/v2/pokemon/2/"}
                  ]
                }
                """;

        PokeApiNamedResourceListResponse listResponse = objectMapper.readValue(json, PokeApiNamedResourceListResponse.class);

        assertThat(listResponse.getCount()).isEqualTo(1302);
        assertThat(listResponse.getNext()).isNotNull();
        assertThat(listResponse.getPrevious()).isNull();
        assertThat(listResponse.getResults()).hasSize(2);
        assertThat(listResponse.getResults().get(0).getName()).isEqualTo("bulbasaur");
    }

    @Test
    void shouldSerializeAndDeserializeRoundTrip() throws Exception {
        PokeApiPokemonResponse original = PokeApiPokemonResponse.builder()
                .id(25).name("pikachu").weight(60).height(4).baseExperience(112).build();

        String json = objectMapper.writeValueAsString(original);
        PokeApiPokemonResponse deserialized = objectMapper.readValue(json, PokeApiPokemonResponse.class);

        assertThat(deserialized.getId()).isEqualTo(original.getId());
        assertThat(deserialized.getName()).isEqualTo(original.getName());
    }

    @Test
    void shouldBuildPokeApiNamedResourceListResponse() {
        PokeApiNamedResourceListResponse response = PokeApiNamedResourceListResponse.builder()
                .count(2)
                .results(List.of(
                        PokeApiNamedResource.builder().name("bulbasaur").url("https://pokeapi.co/api/v2/pokemon/1/").build()
                ))
                .build();

        assertThat(response.getCount()).isEqualTo(2);
        assertThat(response.getResults()).hasSize(1);
    }
}

