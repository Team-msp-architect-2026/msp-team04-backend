package com.moment.momentbackend.payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.payment.dto.TossConfirmApiRequest;
import com.moment.momentbackend.payment.dto.TossConfirmApiResponse;
import com.moment.momentbackend.payment.dto.TossErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class TossPaymentClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String secretKey;

    public TossPaymentClient(
            @Value("${toss.secret-key:}") String secretKey,
            @Value("${toss.api-base-url:https://api.tosspayments.com}") String apiBaseUrl,
            ObjectMapper objectMapper
    ) {
        this.secretKey = secretKey;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(apiBaseUrl)
                .build();
    }

    public TossConfirmApiResponse confirm(TossConfirmApiRequest request) {
        if (!StringUtils.hasText(secretKey)) {
            log.error("TOSS_SECRET_KEY is empty. Check local .env or deployment secret.");
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .header(HttpHeaders.AUTHORIZATION, createAuthorizationHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(TossConfirmApiResponse.class);
        } catch (RestClientResponseException e) {
            log.error(
                    "Toss payment confirm failed. status={}, body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            TossErrorResponse tossError = parseError(e.getResponseBodyAsString());
            if (tossError != null) {
                log.error(
                        "Toss error code={}, message={}",
                        tossError.getCode(),
                        tossError.getMessage()
                );
            }

            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        } catch (Exception e) {
            log.error("Unexpected Toss payment confirm error", e);
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }
    }

    private String createAuthorizationHeader() {
        String raw = secretKey + ":";
        String encoded = Base64.getEncoder()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));

        return "Basic " + encoded;
    }

    private TossErrorResponse parseError(String body) {
        try {
            return objectMapper.readValue(body, TossErrorResponse.class);
        } catch (Exception e) {
            return null;
        }
    }
}