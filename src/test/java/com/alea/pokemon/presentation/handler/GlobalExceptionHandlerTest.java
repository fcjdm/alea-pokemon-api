package com.alea.pokemon.presentation.handler;

import com.alea.pokemon.domain.exception.PokemonNotFoundException;
import com.alea.pokemon.domain.exception.PokemonServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 * Verifies RFC 7807 ProblemDetail construction for each exception type.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturn404ForPokemonNotFoundById() {
        PokemonNotFoundException ex = new PokemonNotFoundException(42);
        ProblemDetail detail = handler.handlePokemonNotFoundException(ex);

        assertThat(detail.getStatus()).isEqualTo(404);
        assertThat(detail.getTitle()).isEqualTo("Pokémon Not Found");
        assertThat(detail.getDetail()).contains("42");
        assertThat(detail.getProperties()).containsKey("timestamp");
    }

    @Test
    void shouldReturn404ForPokemonNotFoundByName() {
        PokemonNotFoundException ex = new PokemonNotFoundException("mew");
        ProblemDetail detail = handler.handlePokemonNotFoundException(ex);

        assertThat(detail.getStatus()).isEqualTo(404);
        assertThat(detail.getDetail()).contains("mew");
    }

    @Test
    void shouldReturn502ForPokemonServiceUnavailable() {
        PokemonServiceUnavailableException ex = new PokemonServiceUnavailableException("PokéAPI is down");
        ProblemDetail detail = handler.handlePokemonServiceUnavailableException(ex);

        assertThat(detail.getStatus()).isEqualTo(502);
        assertThat(detail.getTitle()).isEqualTo("Bad Gateway");
        assertThat(detail.getDetail()).contains("PokéAPI is down");
        assertThat(detail.getProperties()).containsKey("timestamp");
    }

    @Test
    void shouldReturn502ForPokemonServiceUnavailableWithCause() {
        PokemonServiceUnavailableException ex = new PokemonServiceUnavailableException(
                "timeout", new RuntimeException("connect timeout"));
        ProblemDetail detail = handler.handlePokemonServiceUnavailableException(ex);

        assertThat(detail.getStatus()).isEqualTo(502);
    }

    @Test
    void shouldReturn500ForUnexpectedException() {
        Exception ex = new RuntimeException("Something went wrong");
        ProblemDetail detail = handler.handleGenericException(ex);

        assertThat(detail.getStatus()).isEqualTo(500);
        assertThat(detail.getTitle()).isEqualTo("Internal Server Error");
        assertThat(detail.getDetail()).contains("unexpected error");
        assertThat(detail.getProperties()).containsKey("timestamp");
    }
}

