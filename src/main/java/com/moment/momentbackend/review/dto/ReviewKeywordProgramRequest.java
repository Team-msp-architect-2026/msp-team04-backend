package com.moment.momentbackend.review.dto;

public record ReviewKeywordProgramRequest(
        Long programId,
        String title,
        String category,
        Double ratingAvg,
        Integer reviewCount
) {
}
