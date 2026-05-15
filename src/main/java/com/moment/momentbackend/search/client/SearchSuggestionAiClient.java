package com.moment.momentbackend.search.client;

import com.moment.momentbackend.search.dto.SearchSuggestionAiRequest;
import com.moment.momentbackend.search.dto.SearchSuggestionAiResponse;
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
public class SearchSuggestionAiClient {

    private final RestClient restClient;
    private final String serviceBaseUrl;

    public SearchSuggestionAiClient(
            @Value("${ai.service-base-url:}") String serviceBaseUrl
    ) {
        this.serviceBaseUrl = serviceBaseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(StringUtils.hasText(serviceBaseUrl)
                        ? serviceBaseUrl
                        : "http://localhost:8000")
                .build();
    }

    public Optional<SearchSuggestionAiResponse> generateSuggestions(
            SearchSuggestionAiRequest request
    ) {
        if (!StringUtils.hasText(serviceBaseUrl)) {
            log.warn("AI service base URL is empty. Use fallback search suggestions.");
        }

        try {
            SearchSuggestionAiResponse response = restClient.post()
                    .uri("/internal/ai/search-suggestions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(SearchSuggestionAiResponse.class);

            return Optional.ofNullable(response);
        } catch (RestClientResponseException e) {
            log.warn(
                    "moment-ai search suggestion request failed. status={}, body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Unexpected moment-ai search suggestion error", e);
            return Optional.empty();
        }
    }
}
