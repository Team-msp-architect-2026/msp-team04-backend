package com.moment.momentbackend.recommendation.dto;

import java.util.List;

public record NextRecommendExplainResponse(
        String message,
        List<NextRecommendExplainItemResponse> items,
        String source
) {
}
