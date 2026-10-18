package com.alea.pokemon.presentation.handler;

import com.alea.pokemon.domain.exception.PokemonNotFoundException;
import com.alea.pokemon.domain.exception.PokemonServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

/**
 * Centralized exception handler returning RFC 7807 {@link ProblemDetail} responses
 * for all domain and unexpected errors.
 *
 * <p>Maps:</p>
 * <ul>
 *   <li>{@link PokemonNotFoundException} → 404</li>
 *   <li>{@link PokemonServiceUnavailableException} → 502</li>
 *   <li>{@link Exception} (fallback) → 500</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final URI BLANK_TYPE = URI.create("about:blank");

    /**
     * Handles {@link PokemonNotFoundException} — returns 404 with RFC 7807 body.
     */
    @ExceptionHandler(PokemonNotFoundException.class)
    public ProblemDetail handlePokemonNotFoundException(PokemonNotFoundException ex) {
        log.warn("Pokémon not found: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setType(BLANK_TYPE);
        problemDetail.setTitle("Pokémon Not Found");
        problemDetail.setProperty("timestamp", Instant.now().toString());
        return problemDetail;
    }

    /**
     * Handles {@link PokemonServiceUnavailableException} — returns 502 with RFC 7807 body.
     */
    @ExceptionHandler(PokemonServiceUnavailableException.class)
    public ProblemDetail handlePokemonServiceUnavailableException(PokemonServiceUnavailableException ex) {
        log.error("Upstream PokéAPI service unavailable: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        problemDetail.setType(BLANK_TYPE);
        problemDetail.setTitle("Bad Gateway");
        problemDetail.setProperty("timestamp", Instant.now().toString());
        return problemDetail;
    }

    /**
     * Fallback handler for all unhandled exceptions — returns 500 with RFC 7807 body.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
        problemDetail.setType(BLANK_TYPE);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now().toString());
        return problemDetail;
    }
}

