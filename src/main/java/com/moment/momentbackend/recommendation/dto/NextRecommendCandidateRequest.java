package com.moment.momentbackend.recommendation.dto;

public record NextRecommendCandidateRequest(
        Long programId,
        String title,
        String category,
        String description,
        String classTime,
        Integer price,
        Boolean isFree,
        Double ratingAvg,
        String reasonBasis
) {
}
