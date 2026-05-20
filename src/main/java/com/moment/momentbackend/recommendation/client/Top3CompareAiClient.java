package com.moment.momentbackend.recommendation.client;

import com.moment.momentbackend.recommendation.dto.Top3CompareAiRequest;
import com.moment.momentbackend.recommendation.dto.Top3CompareResponse;
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
public class Top3CompareAiClient {

    private final RestClient restClient;
    private final String serviceBaseUrl;

    public Top3CompareAiClient(
            @Value("${ai.service-base-url:}") String serviceBaseUrl
    ) {
        this.serviceBaseUrl = serviceBaseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(StringUtils.hasText(serviceBaseUrl)
                        ? serviceBaseUrl
                        : "http://localhost:8000")
                .build();
    }

    public Optional<Top3CompareResponse> compare(Top3CompareAiRequest request) {
        if (!StringUtils.hasText(serviceBaseUrl)) {
            log.warn("AI service base URL is empty. Use fallback top3 compare.");
        }

        long startedAt = System.currentTimeMillis();

        try {
            Top3CompareResponse response = restClient.post()
                    .uri("/internal/ai/top3-compare")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(Top3CompareResponse.class);

            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.info(
                    "[Top3Compare] moment-ai request success. preferenceId={}, programCount={}, elapsedMs={}",
                    request.preference() != null ? request.preference().preferenceId() : null,
                    request.programs() != null ? request.programs().size() : 0,
                    elapsedMs
            );

            return Optional.ofNullable(response);
        } catch (RestClientResponseException e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.warn(
                    "[Top3Compare] moment-ai request failed. status={}, elapsedMs={}, body={}",
                    e.getStatusCode(),
                    elapsedMs,
                    e.getResponseBodyAsString()
            );
            return Optional.empty();
        } catch (Exception e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.warn(
                    "[Top3Compare] unexpected moment-ai error. elapsedMs={}",
                    elapsedMs,
                    e
            );
            return Optional.empty();
        }
    }
}
