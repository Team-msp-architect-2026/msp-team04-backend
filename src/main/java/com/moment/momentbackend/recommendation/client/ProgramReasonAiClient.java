package com.moment.momentbackend.recommendation.client;

import com.moment.momentbackend.recommendation.dto.ProgramReasonAiRequest;
import com.moment.momentbackend.recommendation.dto.ProgramReasonResponse;
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
public class ProgramReasonAiClient {

    private final RestClient restClient;
    private final String serviceBaseUrl;

    public ProgramReasonAiClient(
            @Value("${ai.service-base-url:}") String serviceBaseUrl
    ) {
        this.serviceBaseUrl = serviceBaseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(StringUtils.hasText(serviceBaseUrl)
                        ? serviceBaseUrl
                        : "http://localhost:8000")
                .build();
    }

    public Optional<ProgramReasonResponse> generate(ProgramReasonAiRequest request) {
        if (!StringUtils.hasText(serviceBaseUrl)) {
            log.warn("AI service base URL is empty. Use fallback program reason.");
        }

        long startedAt = System.currentTimeMillis();

        try {
            ProgramReasonResponse response = restClient.post()
                    .uri("/internal/ai/program-reason")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ProgramReasonResponse.class);

            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.info(
                    "[ProgramReason] moment-ai request success. preferenceId={}, programId={}, elapsedMs={}",
                    request.preference() != null ? request.preference().preferenceId() : null,
                    request.program() != null ? request.program().programId() : null,
                    elapsedMs
            );

            return Optional.ofNullable(response);
        } catch (RestClientResponseException e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.warn(
                    "[ProgramReason] moment-ai request failed. status={}, elapsedMs={}, body={}",
                    e.getStatusCode(),
                    elapsedMs,
                    e.getResponseBodyAsString()
            );
            return Optional.empty();
        } catch (Exception e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.warn(
                    "[ProgramReason] unexpected moment-ai error. elapsedMs={}",
                    elapsedMs,
                    e
            );
            return Optional.empty();
        }
    }
}
