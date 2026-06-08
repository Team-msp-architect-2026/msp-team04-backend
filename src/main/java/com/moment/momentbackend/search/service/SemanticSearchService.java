package com.moment.momentbackend.search.service;

import com.moment.momentbackend.embedding.client.EmbeddingServiceClient;
import com.moment.momentbackend.embedding.dto.EmbeddingRequestDto;
import com.moment.momentbackend.embedding.dto.EmbeddingResponseDto;
import com.moment.momentbackend.global.metrics.BusinessMetricsService;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.search.client.RerankerClient;
import com.moment.momentbackend.search.dto.RerankCandidateRequest;
import com.moment.momentbackend.search.dto.RerankRequest;
import com.moment.momentbackend.search.dto.RerankResponse;
import com.moment.momentbackend.search.dto.RerankResult;
import com.moment.momentbackend.search.dto.SemanticSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticSearchService {

    private static final String PROGRAM_INDEX = "program_vector";
    private static final String VECTOR_FIELD = "vector";
    private static final int TOP_K = 20;

    private final EmbeddingServiceClient embeddingServiceClient;
    private final OpenSearchClient openSearchClient;
    private final ProgramRepository programRepository;
    private final RerankerClient rerankerClient;
    private final BusinessMetricsService businessMetricsService;

    public List<SemanticSearchResponseDto> search(String query) {
        return businessMetricsService.recordSearch(
                "semantic",
                () -> searchInternal(query)
        );
    }

    private List<SemanticSearchResponseDto> searchInternal(String query) {
        String normalizedQuery = normalizeQuery(query);
        long startedAt = System.currentTimeMillis();

        EmbeddingResponseDto embeddingResponse = embeddingServiceClient.embed(
                new EmbeddingRequestDto(0L, "QUERY", normalizedQuery)
        );

        if (!embeddingResponse.isSuccess() || embeddingResponse.getVector() == null) {
            log.warn("[시맨틱 검색] 임베딩 실패 query={}", normalizedQuery);
            businessMetricsService.recordSearchSource("semantic", "embedding_failed");
            return List.of();
        }

        Map<Long, Double> scoreMap = searchOpenSearch(normalizedQuery, embeddingResponse.getVector());

        if (scoreMap.isEmpty()) {
            businessMetricsService.recordSearchSource("semantic", "opensearch_empty");
            return List.of();
        }

        Map<Long, Program> programMap = programRepository.findAllById(scoreMap.keySet())
                .stream()
                .collect(Collectors.toMap(Program::getId, program -> program));

        List<SemanticSearchResponseDto> openSearchResults = scoreMap.entrySet().stream()
                .filter(entry -> programMap.containsKey(entry.getKey()))
                .map(entry -> toResponse(programMap.get(entry.getKey()), entry.getValue(), null))
                .toList();

        List<SemanticSearchResponseDto> rerankedResults = applyRerank(
                normalizedQuery,
                openSearchResults,
                programMap
        );

        long elapsedMs = System.currentTimeMillis() - startedAt;

        log.info(
                "[시맨틱 검색] query={}, candidates={}, elapsedMs={}",
                normalizedQuery,
                rerankedResults.size(),
                elapsedMs
        );

        businessMetricsService.recordSearchSource("semantic", "opensearch");

        return rerankedResults;
    }

    private Map<Long, Double> searchOpenSearch(String query, float[] queryVector) {
        Map<Long, Double> scoreMap = new LinkedHashMap<>();

        try {
            SearchRequest request = SearchRequest.of(requestBuilder -> requestBuilder
                    .index(PROGRAM_INDEX)
                    .query(queryBuilder -> queryBuilder
                            .knn(knnBuilder -> knnBuilder
                                    .field(VECTOR_FIELD)
                                    .vector(queryVector)
                                    .k(TOP_K)
                            )
                    )
                    .size(TOP_K)
            );

            SearchResponse<Map> response = openSearchClient.search(
                    request,
                    (Class<Map>) (Class<?>) Map.class
            );

            for (Hit<Map> hit : response.hits().hits()) {
                if (hit.source() == null) {
                    continue;
                }

                Object programIdObj = hit.source().get("program_id");

                if (programIdObj == null) {
                    continue;
                }

                Long programId = Long.valueOf(programIdObj.toString());
                double score = hit.score() != null ? hit.score() : 0.0;

                scoreMap.put(programId, score);
            }

            return scoreMap;
        } catch (IOException e) {
            log.error("[시맨틱 검색] OpenSearch 조회 실패 query={} error={}", query, e.getMessage());
            return Map.of();
        }
    }

    private List<SemanticSearchResponseDto> applyRerank(
            String query,
            List<SemanticSearchResponseDto> openSearchResults,
            Map<Long, Program> programMap
    ) {
        if (openSearchResults.isEmpty()) {
            return openSearchResults;
        }

        List<RerankCandidateRequest> candidates = openSearchResults.stream()
                .map(result -> {
                    Program program = programMap.get(result.getProgramId());

                    return new RerankCandidateRequest(
                            result.getProgramId(),
                            result.getTitle(),
                            program != null ? program.getDescription() : "",
                            program != null ? buildReviewSummary(program) : "",
                            result.getSemanticScore()
                    );
                })
                .toList();

        return rerankerClient.rerank(new RerankRequest(query, candidates))
                .map(response -> mergeRerankResults(openSearchResults, response))
                .orElseGet(() -> {
                    log.warn("[Reranker] fallback to OpenSearch order. query={}", query);
                    return openSearchResults;
                });
    }

    private List<SemanticSearchResponseDto> mergeRerankResults(
            List<SemanticSearchResponseDto> openSearchResults,
            RerankResponse response
    ) {
        if (response.results() == null || response.results().isEmpty()) {
            log.warn("[Reranker] empty response. fallback to OpenSearch order.");
            return openSearchResults;
        }

        Map<Long, Double> rerankScoreMap = response.results().stream()
                .filter(result -> result.candidateId() != null)
                .filter(result -> result.rerankScore() != null)
                .collect(Collectors.toMap(
                        RerankResult::candidateId,
                        RerankResult::rerankScore,
                        (left, right) -> left
                ));

        if (rerankScoreMap.size() != openSearchResults.size()) {
            log.warn(
                    "[Reranker] response size mismatch. expected={}, actual={}. fallback to OpenSearch order.",
                    openSearchResults.size(),
                    rerankScoreMap.size()
            );
            return openSearchResults;
        }

        return openSearchResults.stream()
                .map(result -> result.toBuilder()
                        .rerankScore(rerankScoreMap.get(result.getProgramId()))
                        .build())
                .sorted(Comparator
                        .comparing(
                                SemanticSearchResponseDto::getRerankScore,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                        .thenComparing(
                                SemanticSearchResponseDto::getSemanticScore,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                )
                .toList();
    }

    private SemanticSearchResponseDto toResponse(
            Program program,
            Double semanticScore,
            Double rerankScore
    ) {
        return SemanticSearchResponseDto.builder()
                .programId(program.getId())
                .title(program.getTitle())
                .category(program.getCategory())
                .region(program.getRegion())
                .price(program.getPrice())
                .isFree(program.getIsFree())
                .imageUrl(program.getImageUrl())
                .ratingAvg(program.getRatingAvg())
                .reviewCount(program.getReviewCount())
                .isRecruiting(program.getIsRecruiting())
                .semanticScore(semanticScore)
                .rerankScore(rerankScore)
                .build();
    }

    private String buildReviewSummary(Program program) {
        Integer reviewCount = program.getReviewCount();
        BigDecimal ratingAvg = program.getRatingAvg();

        if (reviewCount == null || reviewCount <= 0 || ratingAvg == null) {
            return "";
        }

        return "평균 평점 " + ratingAvg + "점, 후기 " + reviewCount + "개";
    }

    private String normalizeQuery(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query must not be blank.");
        }

        return query.trim().replaceAll("\\s+", " ");
    }
}
