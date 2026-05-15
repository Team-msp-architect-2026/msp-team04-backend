package com.moment.momentbackend.embedding.dto;

public record EmbeddingJobResponseDto(
        String target,
        int totalCount,
        int successCount,
        int failCount
) {
}
