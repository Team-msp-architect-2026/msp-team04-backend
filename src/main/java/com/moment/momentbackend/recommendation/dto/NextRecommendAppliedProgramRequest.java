package com.moment.momentbackend.recommendation.dto;

public record NextRecommendAppliedProgramRequest(
        Long programId,
        String title,
        String category,
        String description,
        String classTime,
        Integer price,
        Boolean isFree,
        Double ratingAvg
) {
}
