package com.moment.momentbackend.report.client;

import com.moment.momentbackend.report.dto.ParentingReportAiRequest;
import com.moment.momentbackend.report.dto.ParentingReportAiResponse;
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
public class ParentingReportAiClient {

    private final RestClient restClient;
    private final String serviceBaseUrl;

    public ParentingReportAiClient(
            @Value("${ai.service-base-url:}") String serviceBaseUrl
    ) {
        this.serviceBaseUrl = serviceBaseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(StringUtils.hasText(serviceBaseUrl)
                        ? serviceBaseUrl
                        : "http://localhost:8000")
                .build();
    }

    public Optional<ParentingReportAiResponse> generate(ParentingReportAiRequest request) {
        if (!StringUtils.hasText(serviceBaseUrl)) {
            log.warn("AI service base URL is empty. Use fallback parenting report.");
        }

        long startedAt = System.currentTimeMillis();

        try {
            ParentingReportAiResponse response = restClient.post()
                    .uri("/internal/ai/parenting-report")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ParentingReportAiResponse.class);

            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.info(
                    "[ParentingReport] moment-ai request success. childName={}, supportCount={}, elapsedMs={}",
                    request.childInfo() != null ? request.childInfo().childName() : null,
                    request.supportCount(),
                    elapsedMs
            );

            return Optional.ofNullable(response);
        } catch (RestClientResponseException e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.warn(
                    "[ParentingReport] moment-ai request failed. status={}, elapsedMs={}, body={}",
                    e.getStatusCode(),
                    elapsedMs,
                    e.getResponseBodyAsString()
            );
            return Optional.empty();
        } catch (Exception e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;

            log.warn(
                    "[ParentingReport] unexpected moment-ai error. elapsedMs={}",
                    elapsedMs,
                    e
            );
            return Optional.empty();
        }
    }
}
