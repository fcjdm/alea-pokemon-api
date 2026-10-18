package com.alea.pokemon.presentation.controller;

import com.alea.pokemon.application.dto.PokemonResponseDto;
import com.alea.pokemon.application.service.PokemonRankingService;
import com.alea.pokemon.presentation.api.PokemonApi;
import com.alea.pokemon.presentation.dto.PokemonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller implementing the OpenAPI-generated {@link PokemonApi} contract.
 *
 * <p>All HTTP routing, validations, and Swagger metadata are defined by the contract
 * generated from {@code src/main/resources/openapi/openapi.yml}.</p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PokemonController implements PokemonApi {

    private final PokemonRankingService pokemonRankingService;

    @Override
    public ResponseEntity<List<PokemonResponse>> getHeaviestPokemons() {
        log.info("GET /api/v1/pokemon/heaviest");
        return ResponseEntity.ok(mapToPresentation(pokemonRankingService.getHeaviest()));
    }

    @Override
    public ResponseEntity<List<PokemonResponse>> getHighestPokemons() {
        log.info("GET /api/v1/pokemon/highest");
        return ResponseEntity.ok(mapToPresentation(pokemonRankingService.getHighest()));
    }

    @Override
    public ResponseEntity<List<PokemonResponse>> getExperiencePokemons() {
        log.info("GET /api/v1/pokemon/experience");
        return ResponseEntity.ok(mapToPresentation(pokemonRankingService.getTopExperience()));
    }

    private List<PokemonResponse> mapToPresentation(List<PokemonResponseDto> dtos) {
        return dtos.stream()
                .map(dto -> new PokemonResponse()
                        .id(dto.getId())
                        .name(dto.getName())
                        .weight(dto.getWeight())
                        .height(dto.getHeight())
                        .baseExperience(dto.getBaseExperience()))
                .toList();
    }
}

