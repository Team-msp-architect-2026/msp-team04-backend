package com.moment.momentbackend.embedding.dto;

public record EmbeddingAiRequest(
        Long sourceId,
        String sourceType,
        String text
) {
}
