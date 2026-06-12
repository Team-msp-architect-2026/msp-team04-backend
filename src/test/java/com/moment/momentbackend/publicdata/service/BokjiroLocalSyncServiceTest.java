package com.moment.momentbackend.publicdata.service;

import com.moment.momentbackend.publicdata.client.BokjiroLocalClient;
import com.moment.momentbackend.publicdata.dto.BokjiroLocalResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("external-api")
@SpringBootTest
@ActiveProfiles("local")
class BokjiroLocalSyncServiceTest {

    @Autowired
    private BokjiroLocalSyncService syncService;

    @Autowired
    private BokjiroLocalClient client;

    @Value("${public.data.data-go-kr.service-key}")
    private String serviceKey;

    @Test
    void raw_XML_확인() {
        String url = "http://apis.data.go.kr/B554287/LocalWelfareInformationsV001/LcgvWelfarelist"
                + "?serviceKey=" + serviceKey + "&pageNo=1&numOfRows=3&srchKeyCode=001";

        System.out.println("=== 요청 URL ===");
        System.out.println(url);

        RestTemplate restTemplate = new RestTemplate();
        String raw = restTemplate.getForObject(url, String.class);

        System.out.println("=== RAW XML ===");
        System.out.println(raw);
        System.out.println("=== END ===");
    }

    @Test
    void 첫_페이지_파싱_확인() {
        BokjiroLocalResponse response = client.fetchPage(1, 10);

        System.out.println("totalCount: " + response.getTotalCount());
        System.out.println("resultCode: " + response.getResultCode());
        System.out.println("resultMessage: " + response.getResultMessage());
        System.out.println("servList: " + response.getServList());

        assertThat(response.getServList()).isNotEmpty();
    }

    @Test
    void 전체_수집() {
        syncService.syncAll(100);
    }
}