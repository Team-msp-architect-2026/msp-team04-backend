package com.moment.momentbackend.publicdata.client;

import com.moment.momentbackend.publicdata.dto.GovBenefitApiResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("external-api")
@SpringBootTest
class GovBenefitSyncServiceTest {

    @Autowired
    private GovBenefitClient govBenefitClient;

    @Test
    void 공공서비스_혜택_API_호출_테스트() {
        GovBenefitApiResponse response = govBenefitClient.fetchBenefits(1, 10);

        System.out.println("총 건수: " + response.getTotalCount());
        System.out.println("현재 페이지 건수: " + response.getCurrentCount());

        if (response.getData() != null) {
            response.getData().forEach(item ->
                    System.out.println("서비스명: " + item.getServiceName())
            );
        }
    }
}