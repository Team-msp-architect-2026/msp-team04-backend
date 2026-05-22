package com.moment.momentbackend.recommendation.client;

import com.moment.momentbackend.recommendation.dto.NextRecommendAiRequest;
import com.moment.momentbackend.recommendation.dto.NextRecommendExplainResponse;
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
public class NextRecommendAiClient {

    private final RestClient restClient;
    private final String serviceBaseUrl;

    public NextRecommendAiClient(
            @Value("${ai.service-base-url:}") String serviceBaseUrl
    ) {
        this.serviceBaseUrl = serviceBaseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(StringUtils.hasText(serviceBaseUrl)
                        ? serviceBaseUrl
                        : "http://localhost:8000")
                .build();
    }

    public Optional<NextRecommendExplainResponse> generate(NextRecommendAiRequest request) {
        if (!StringUtils.hasText(serviceBaseUrl)) {
            log.warn("AI service base URL is empty. Use fallback next recommend.");
        }

        long startedAt = System.currentTimeMillis();

        try {
            NextRecommendExplainResponse response = restClient.post()
                    .uri("/internal/ai/next-recommend")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(NextRecommendExplainResponse.class);

            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.info(
                    "[NextRecommend] moment-ai request success. appliedProgramId={}, candidateCount={}, elapsedMs={}",
                    request.appliedProgram() != null ? request.appliedProgram().programId() : null,
                    request.candidates() != null ? request.candidates().size() : 0,
                    elapsedMs
            );

            return Optional.ofNullable(response);
        } catch (RestClientResponseException e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.warn(
                    "[NextRecommend] moment-ai request failed. status={}, elapsedMs={}, body={}",
                    e.getStatusCode(),
                    elapsedMs,
                    e.getResponseBodyAsString()
            );
            return Optional.empty();
        } catch (Exception e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.warn(
                    "[NextRecommend] unexpected moment-ai error. elapsedMs={}",
                    elapsedMs,
                    e
            );
            return Optional.empty();
        }
    }
}
