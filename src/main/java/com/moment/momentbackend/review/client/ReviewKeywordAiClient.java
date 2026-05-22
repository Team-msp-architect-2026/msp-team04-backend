package com.moment.momentbackend.review.client;

import com.moment.momentbackend.review.dto.ReviewKeywordAiRequest;
import com.moment.momentbackend.review.dto.ReviewKeywordAiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

@Slf4j
@Component
public class ReviewKeywordAiClient {

    private final RestClient restClient;
    private final String serviceBaseUrl;

    public ReviewKeywordAiClient(
            @Value("${ai.service-base-url:}") String serviceBaseUrl
    ) {
        this.serviceBaseUrl = serviceBaseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(StringUtils.hasText(serviceBaseUrl)
                        ? serviceBaseUrl
                        : "http://localhost:8000")
                .build();
    }

    public Optional<ReviewKeywordAiResponse> generate(ReviewKeywordAiRequest request) {
        if (!StringUtils.hasText(serviceBaseUrl)) {
            log.warn("AI service base URL is empty. Use fallback review keywords.");
        }

        long startedAt = System.currentTimeMillis();

        try {
            ReviewKeywordAiResponse response = restClient.post()
                    .uri("/internal/ai/review-keywords")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ReviewKeywordAiResponse.class);

            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.info(
                    "[ReviewKeywords] moment-ai request success. programId={}, reviewCount={}, elapsedMs={}",
                    request.program() != null ? request.program().programId() : null,
                    request.stats() != null ? request.stats().reviewCount() : null,
                    elapsedMs
            );

            return Optional.ofNullable(response);
        } catch (RestClientResponseException e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.warn(
                    "[ReviewKeywords] moment-ai request failed. status={}, elapsedMs={}, body={}",
                    e.getStatusCode(),
                    elapsedMs,
                    e.getResponseBodyAsString()
            );
            return Optional.empty();
        } catch (Exception e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.warn(
                    "[ReviewKeywords] unexpected moment-ai error. elapsedMs={}",
                    elapsedMs,
                    e
            );
            return Optional.empty();
        }
    }
}
