package com.moment.momentbackend.search.dto;

public record RerankCandidateRequest(
        Long candidateId,
        String title,
        String description,
        String reviewSummary,
        Double semanticScore
) {
}
