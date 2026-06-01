package com.moment.momentbackend.publicdata.client;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.moment.momentbackend.publicdata.dto.BokjiroLocalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class BokjiroLocalClient {

    private final XmlMapper xmlMapper = new XmlMapper();

    private static final String BASE_URL =
            "https://apis.data.go.kr/B554287/LocalGovernmentWelfareInformations/LcgvWelfarelist";

    @Value("${public.data.data-go-kr.service-key}")
    private String serviceKey;

    public BokjiroLocalResponse fetchPage(int pageNo, int numOfRows) {
        String url = BASE_URL
                + "?serviceKey=" + serviceKey
                + "&pageNo=" + pageNo
                + "&numOfRows=" + numOfRows
                + "&srchKeyCode=001";

        log.info("[BokjiroLocal] 요청 - page: {}, size: {}", pageNo, numOfRows);

        RestTemplate restTemplate = new RestTemplate();
        String xmlResponse = restTemplate.getForObject(url, String.class);

        log.info("[BokjiroLocal] 응답:\n{}", xmlResponse);

        try {
            return xmlMapper.readValue(xmlResponse, BokjiroLocalResponse.class);
        } catch (Exception e) {
            log.error("[BokjiroLocal] XML 파싱 실패. 응답:\n{}", xmlResponse);
            throw new RuntimeException("BokjiroLocal XML 파싱 오류", e);
        }
    }
}