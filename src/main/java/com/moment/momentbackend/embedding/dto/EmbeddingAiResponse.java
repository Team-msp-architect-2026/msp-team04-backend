package com.moment.momentbackend.embedding.dto;

import java.util.List;

public record EmbeddingAiResponse(
        Long sourceId,
        String sourceType,
        List<Double> vector,
        boolean success
) {
}
