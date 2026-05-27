package com.moment.momentbackend.recommendation.dto;

public record NextRecommendExplainItemResponse(
        Long programId,
        String title,
        String explainMessage,
        String highlightTag
) {
}
