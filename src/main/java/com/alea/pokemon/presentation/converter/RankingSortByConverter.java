package com.alea.pokemon.presentation.converter;

import com.alea.pokemon.application.strategy.RankingCriteria;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RankingSortByConverter implements Converter<String, RankingCriteria> {

    @Override
    public RankingCriteria convert(String source) {
        return switch (source) {
            case "weight" -> RankingCriteria.HEAVIEST;
            case "height" -> RankingCriteria.HIGHEST;
            case "experience" -> RankingCriteria.EXPERIENCE;
            default -> throw new IllegalArgumentException("Unsupported sortBy value: " + source);
        };
    }
}
