package com.moment.momentbackend.embedding.client;

import com.moment.momentbackend.embedding.dto.EmbeddingAiRequest;
import com.moment.momentbackend.embedding.dto.EmbeddingAiResponse;
import com.moment.momentbackend.embedding.dto.EmbeddingRequestDto;
import com.moment.momentbackend.embedding.dto.EmbeddingResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
public class EmbeddingServiceClient {

    private static final int MAX_RETRY = 3;

    private final RestClient restClient;
    private final String serviceBaseUrl;

    public EmbeddingServiceClient(
            @Value("${ai.service-base-url:}") String serviceBaseUrl
    ) {
        this.serviceBaseUrl = serviceBaseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(StringUtils.hasText(serviceBaseUrl)
                        ? serviceBaseUrl
                        : "http://localhost:8000")
                .build();
    }

    public EmbeddingResponseDto embed(EmbeddingRequestDto request) {
        if (!StringUtils.hasText(serviceBaseUrl)) {
            log.warn("AI service base URL is empty. Use default local AI service.");
        }

        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                log.info(
                        "[임베딩 요청 {}/{}] sourceType={}, sourceId={}",
                        attempt,
                        MAX_RETRY,
                        request.getSourceType(),
                        request.getSourceId()
                );

                EmbeddingAiResponse response = restClient.post()
                        .uri("/internal/ai/embeddings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new EmbeddingAiRequest(
                                request.getSourceId(),
                                request.getSourceType(),
                                request.getText()
                        ))
                        .retrieve()
                        .body(EmbeddingAiResponse.class);

                if (response == null || !response.success() || response.vector() == null || response.vector().isEmpty()) {
                    log.warn(
                            "[임베딩 응답 비정상] sourceType={}, sourceId={}",
                            request.getSourceType(),
                            request.getSourceId()
                    );
                    continue;
                }

                return new EmbeddingResponseDto(
                        response.sourceId(),
                        response.sourceType(),
                        toFloatArray(response.vector()),
                        true
                );

            } catch (RestClientResponseException e) {
                log.warn(
                        "[임베딩 요청 실패 {}/{}] status={}, body={}",
                        attempt,
                        MAX_RETRY,
                        e.getStatusCode(),
                        e.getResponseBodyAsString()
                );
            } catch (Exception e) {
                log.warn(
                        "[임베딩 요청 예외 {}/{}] sourceType={}, sourceId={}, error={}",
                        attempt,
                        MAX_RETRY,
                        request.getSourceType(),
                        request.getSourceId(),
                        e.getMessage()
                );
            }
        }

        log.error(
                "[임베딩 최종 실패] sourceType={}, sourceId={}",
                request.getSourceType(),
                request.getSourceId()
        );

        return new EmbeddingResponseDto(
                request.getSourceId(),
                request.getSourceType(),
                null,
                false
        );
    }

    private float[] toFloatArray(java.util.List<Double> vector) {
        float[] result = new float[vector.size()];

        for (int i = 0; i < vector.size(); i++) {
            result[i] = vector.get(i).floatValue();
        }

        return result;
    }
}
