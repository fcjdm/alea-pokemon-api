package com.alea.pokemon.presentation.controller;

import com.alea.pokemon.application.service.PokemonRankingService;
import com.alea.pokemon.application.strategy.RankingCriteria;
import com.alea.pokemon.presentation.api.PokemonApi;
import com.alea.pokemon.presentation.dto.PokemonResponse;
import com.alea.pokemon.presentation.mapper.PokemonResponseMapper;
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
    private final PokemonResponseMapper pokemonResponseMapper;

    @Override
    public ResponseEntity<List<PokemonResponse>> getPokemonRanking(RankingCriteria sortBy) {
        log.info("GET /api/v1/pokemon?sortBy={}", sortBy);
        return ResponseEntity.ok(pokemonResponseMapper.mapToPresentation(
                pokemonRankingService.getRanking(sortBy)));
    }
}
