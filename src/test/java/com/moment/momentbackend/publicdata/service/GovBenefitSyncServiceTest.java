package com.moment.momentbackend.publicdata.service;

import com.moment.momentbackend.publicdata.repository.GovBenefitRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("external-api")
@SpringBootTest
@ActiveProfiles("local")
class GovBenefitSyncServiceTest {

    @Autowired
    private GovBenefitSyncService govBenefitSyncService;

    @Autowired
    private GovBenefitRepository govBenefitRepository;

    @Test
    void 공공서비스_혜택_데이터를_DB에_저장한다() {
        int savedCount = govBenefitSyncService.syncBenefits(1, 3);

        System.out.println("저장/갱신 건수: " + savedCount);
        System.out.println("DB 전체 건수: " + govBenefitRepository.count());

        assertThat(savedCount).isGreaterThan(0);
        assertThat(govBenefitRepository.count()).isGreaterThan(0);
    }

    @Test
    void 공공서비스_혜택_전체_페이지를_DB에_저장한다() {
        int savedCount = govBenefitSyncService.syncAllBenefits(100);

        System.out.println("전체 저장/갱신 건수: " + savedCount);
        System.out.println("DB 전체 건수: " + govBenefitRepository.count());

        assertThat(savedCount).isGreaterThan(0);
        assertThat(govBenefitRepository.count()).isGreaterThan(0);
    }
}