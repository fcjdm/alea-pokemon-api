package com.alea.pokemon.presentation.mapper;

import com.alea.pokemon.application.dto.PokemonResponseDto;
import com.alea.pokemon.presentation.dto.PokemonResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PokemonResponseMapper {

    public List<PokemonResponse> mapToPresentation(List<PokemonResponseDto> dtos) {
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
