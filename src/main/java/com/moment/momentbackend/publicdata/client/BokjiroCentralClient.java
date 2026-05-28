package com.moment.momentbackend.publicdata.client;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.moment.momentbackend.publicdata.dto.BokjiroCentralResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class BokjiroCentralClient {

    private final WebClient webClient;
    private final XmlMapper xmlMapper = new XmlMapper();

    @Value("${public.data.data-go-kr.service-key}")
    private String serviceKey;

    public BokjiroCentralResponse fetchWelfareList(int pageNo, int numOfRows) {
        log.info("복지로 중앙부처 복지서비스 호출 - pageNo: {}, numOfRows: {}", pageNo, numOfRows);

        String url = "http://apis.data.go.kr/B554287/NationalWelfareInformationsV001/NationalWelfarelistV001"
                + "?callTp=L"
                + "&pageNo=" + pageNo
                + "&numOfRows=" + numOfRows
                + "&srchKeyCode=001"
                + "&serviceKey=" + serviceKey;

        String xmlResponse = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("API 응답: {}", xmlResponse);

        try {
            return xmlMapper.readValue(xmlResponse, BokjiroCentralResponse.class);
        } catch (Exception e) {
            log.error("XML 파싱 실패: {}", e.getMessage());
            throw new RuntimeException("복지로 API 응답 파싱 실패", e);
        }
    }
}