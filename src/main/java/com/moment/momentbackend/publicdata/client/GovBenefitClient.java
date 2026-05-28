package com.moment.momentbackend.publicdata.client;

import com.moment.momentbackend.publicdata.dto.GovBenefitApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GovBenefitClient {

    private final WebClient webClient;

    @Value("${public.data.data-go-kr.service-key}")
    private String serviceKey;

    public GovBenefitApiResponse fetchBenefits(int page, int perPage) {
        log.info("공공서비스 혜택 정보 호출 - page: {}, perPage: {}", page, perPage);

        String url = "https://api.odcloud.kr/api/gov24/v3/serviceList"
                + "?page=" + page
                + "&perPage=" + perPage
                + "&serviceKey=" + serviceKey;

        log.info("호출 URL: {}", url);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(GovBenefitApiResponse.class)
                .block();
    }
}