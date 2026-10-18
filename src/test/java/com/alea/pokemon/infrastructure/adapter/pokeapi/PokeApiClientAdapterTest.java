package com.alea.pokemon.infrastructure.adapter.pokeapi;

import com.alea.pokemon.domain.exception.PokemonServiceUnavailableException;
import com.alea.pokemon.domain.model.Pokemon;
import com.alea.pokemon.infrastructure.config.PokeApiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit tests for {@link PokeApiClientAdapter} using {@link MockRestServiceServer}
 * to avoid real network calls.
 */
class PokeApiClientAdapterTest {

    private MockRestServiceServer mockServer;
    private PokeApiClientAdapter adapter;

    private static final String BASE_URL = "https://pokeapi.co/api/v2";

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();

        PokeApiProperties properties = new PokeApiProperties();
        properties.setBaseUrl(BASE_URL);
        properties.setFetchLimit(2);

        adapter = new PokeApiClientAdapter(restClient, properties);
    }

    @Test
    void shouldReturnMappedPokemonsWhenApiSucceeds() {
        String listJson = """
                {
                  "count": 2,
                  "next": null,
                  "previous": null,
                  "results": [
                    {"name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/"},
                    {"name": "charizard", "url": "https://pokeapi.co/api/v2/pokemon/6/"}
                  ]
                }
                """;

        String bulbasaurJson = """
                {"id": 1, "name": "bulbasaur", "weight": 69, "height": 7, "base_experience": 64}
                """;

        String charizardJson = """
                {"id": 6, "name": "charizard", "weight": 905, "height": 17, "base_experience": 267}
                """;

        mockServer.expect(requestTo(BASE_URL + "/pokemon?limit=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(listJson, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(BASE_URL + "/pokemon/bulbasaur"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(bulbasaurJson, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(BASE_URL + "/pokemon/charizard"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(charizardJson, MediaType.APPLICATION_JSON));

        List<Pokemon> result = adapter.findAll(2);

        mockServer.verify();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("bulbasaur");
        assertThat(result.get(1).getName()).isEqualTo("charizard");
        assertThat(result.get(1).getWeight()).isEqualTo(905);
    }

    @Test
    void shouldThrowPokemonServiceUnavailableExceptionWhenListApiFails() {
        mockServer.expect(requestTo(BASE_URL + "/pokemon?limit=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThatThrownBy(() -> adapter.findAll(2))
                .isInstanceOf(PokemonServiceUnavailableException.class)
                .hasMessageContaining("Failed to fetch Pokémon data");

        mockServer.verify();
    }

    @Test
    void shouldReturnEmptyListWhenApiReturnsEmptyResults() {
        String emptyJson = """
                {
                  "count": 0,
                  "next": null,
                  "previous": null,
                  "results": []
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/pokemon?limit=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(emptyJson, MediaType.APPLICATION_JSON));

        List<Pokemon> result = adapter.findAll(2);

        mockServer.verify();
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenApiReturnsNullResults() {
        String nullResultsJson = """
                {
                  "count": 0,
                  "next": null,
                  "previous": null,
                  "results": null
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/pokemon?limit=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(nullResultsJson, MediaType.APPLICATION_JSON));

        List<Pokemon> result = adapter.findAll(2);

        mockServer.verify();
        assertThat(result).isEmpty();
    }

    @Test
    void shouldSkipPokemonWhenDetailApiFails() {
        String listJson = """
                {
                  "count": 1,
                  "next": null,
                  "previous": null,
                  "results": [
                    {"name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/"}
                  ]
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/pokemon?limit=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(listJson, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(BASE_URL + "/pokemon/bulbasaur"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        List<Pokemon> result = adapter.findAll(2);

        mockServer.verify();
        assertThat(result).isEmpty();
    }
}

