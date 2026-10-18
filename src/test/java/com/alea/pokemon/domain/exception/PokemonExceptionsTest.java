package com.alea.pokemon.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for domain exceptions.
 */
class PokemonExceptionsTest {

    @Test
    void pokemonNotFoundExceptionWithIdShouldContainId() {
        PokemonNotFoundException ex = new PokemonNotFoundException(42);
        assertThat(ex.getMessage()).contains("42");
        assertThat(ex.getPokemonId()).isEqualTo(42);
    }

    @Test
    void pokemonNotFoundExceptionWithNameShouldContainName() {
        PokemonNotFoundException ex = new PokemonNotFoundException("mewtwo");
        assertThat(ex.getMessage()).contains("mewtwo");
        assertThat(ex.getPokemonId()).isEqualTo(-1);
    }

    @Test
    void pokemonServiceUnavailableExceptionShouldRetainMessage() {
        PokemonServiceUnavailableException ex = new PokemonServiceUnavailableException("PokéAPI is down");
        assertThat(ex.getMessage()).isEqualTo("PokéAPI is down");
    }

    @Test
    void pokemonServiceUnavailableExceptionShouldRetainCause() {
        RuntimeException cause = new RuntimeException("timeout");
        PokemonServiceUnavailableException ex = new PokemonServiceUnavailableException("PokéAPI is down", cause);
        assertThat(ex.getCause()).isSameAs(cause);
    }
}

