package com.moment.momentbackend.recommendation.dto;

public record Top3CompareItemResponse(
        Long programId,
        String oneLineReason,
        String highlightTag
) {
}
