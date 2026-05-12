package com.moment.momentbackend.search.service;

import com.moment.momentbackend.embedding.client.EmbeddingServiceClient;
import com.moment.momentbackend.embedding.dto.EmbeddingRequestDto;
import com.moment.momentbackend.embedding.dto.EmbeddingResponseDto;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.search.dto.SemanticSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticSearchService {

    private static final String PROGRAM_INDEX = "program_vector";
    private static final int TOP_K = 20;

    private final EmbeddingServiceClient embeddingServiceClient;
    private final OpenSearchClient openSearchClient;
    private final ProgramRepository programRepository;

    public List<SemanticSearchResponseDto> search(String query) {

        // 1. 검색어 임베딩
        EmbeddingRequestDto embeddingRequest = new EmbeddingRequestDto(0L, "QUERY", query);
        EmbeddingResponseDto embeddingResponse = embeddingServiceClient.embed(embeddingRequest);

        if (!embeddingResponse.isSuccess() || embeddingResponse.getVector() == null) {
            log.warn("[시맨틱 검색] 임베딩 실패 query={}", query);
            return List.of();
        }

        // float[] → List<Float>
        float[] queryVector = embeddingResponse.getVector();

        // 2. OpenSearch kNN 검색
        Map<Long, Double> scoreMap = new LinkedHashMap<>();
        try {
            SearchRequest request = SearchRequest.of(r -> r
                    .index(PROGRAM_INDEX)
                    .query(q -> q
                            .knn(k -> k
                                    .field("vector")
                                    .vector(queryVector)
                                    .k(TOP_K)
                            )
                    )
                    .size(TOP_K)
            );

            SearchResponse<Map> response = openSearchClient.search(
                    request, (Class<Map>) (Class<?>) Map.class);

            for (Hit<Map> hit : response.hits().hits()) {
                if (hit.source() == null) continue;
                Object programIdObj = hit.source().get("program_id");
                if (programIdObj == null) continue;
                Long programId = Long.valueOf(programIdObj.toString());
                double score = hit.score() != null ? hit.score() : 0.0;
                scoreMap.put(programId, score);
            }

        } catch (IOException e) {
            log.error("[시맨틱 검색] OpenSearch 조회 실패 query={} error={}", query, e.getMessage());
            return List.of();
        }

        // 빈 결과
        if (scoreMap.isEmpty()) {
            return List.of();
        }

        // 3. RDS에서 프로그램 데이터 조회 후 매핑
        Map<Long, Program> programMap = programRepository.findAllById(scoreMap.keySet())
                .stream()
                .collect(Collectors.toMap(Program::getId, p -> p));

        return scoreMap.entrySet().stream()
                .filter(e -> programMap.containsKey(e.getKey()))
                .map(e -> {
                    Program p = programMap.get(e.getKey());
                    return SemanticSearchResponseDto.builder()
                            .programId(p.getId())
                            .title(p.getTitle())
                            .category(p.getCategory())
                            .region(p.getRegion())
                            .price(p.getPrice())
                            .isFree(p.getIsFree())
                            .imageUrl(p.getImageUrl())
                            .ratingAvg(p.getRatingAvg())
                            .reviewCount(p.getReviewCount())
                            .isRecruiting(p.getIsRecruiting())
                            .semanticScore(e.getValue())
                            .build();
                })
                .toList();
    }
}