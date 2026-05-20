package com.moment.momentbackend.recommendation.dto;

import java.util.Map;

public record Top3CompareProgramRequest(
        Long programId,
        String title,
        String category,
        String description,
        String region,
        Integer price,
        Boolean isFree,
        String classType,
        Double ratingAvg,
        Integer reviewCount,
        Integer rankNo,
        Double totalScore,
        String recommendReason,
        Map<String, Double> scoreBreakdown
) {
}
