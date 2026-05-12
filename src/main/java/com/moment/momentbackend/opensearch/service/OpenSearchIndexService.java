package com.moment.momentbackend.opensearch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchIndexService {

    private final OpenSearchClient openSearchClient;

    private static final String PROGRAM_INDEX = "program_vector";
    private static final String REVIEW_INDEX  = "review_vector";

    // ── program_vector upsert ─────────────────────────────────────

    public void upsertProgram(Long programId, String title, String category, float[] vector) {
        Map<String, Object> doc = Map.of(
                "program_id", programId,
                "title",      title,
                "category",   category,
                "vector",     vector
        );
        upsert(PROGRAM_INDEX, String.valueOf(programId), doc, "program_id=" + programId);
    }

    // ── review_vector upsert ──────────────────────────────────────

    public void upsertReview(Long reviewId, Long programId, float rating, float[] vector) {
        Map<String, Object> doc = Map.of(
                "review_id",  reviewId,
                "program_id", programId,
                "rating",     rating,
                "vector",     vector
        );
        upsert(REVIEW_INDEX, String.valueOf(reviewId), doc, "review_id=" + reviewId);
    }

    // ── 공통 upsert ───────────────────────────────────────────────

    private void upsert(String index, String id, Map<String, Object> doc, String logKey) {
        try {
            IndexRequest<Map<String, Object>> request = IndexRequest.of(r -> r
                    .index(index)
                    .id(id)
                    .document(doc)
            );
            openSearchClient.index(request);
            log.info("[OpenSearch] upsert 성공 index={} {}", index, logKey);
        } catch (IOException e) {
            // 색인 실패 시 재처리 대상 로그 기록 (DoD 요구사항)
            log.error("[OpenSearch] 색인 실패 - 재처리 필요 index={} {} error={}", index, logKey, e.getMessage());
        }
    }
}