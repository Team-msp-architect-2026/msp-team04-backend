package com.moment.momentbackend.search.dto;

public record RerankResult(
        Long candidateId,
        Double rerankScore
) {
}
