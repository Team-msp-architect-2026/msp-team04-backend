package com.moment.momentbackend.publicdata.service;

import com.moment.momentbackend.publicdata.repository.BokjiroCentralRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("external-api")
@SpringBootTest
@ActiveProfiles("local")
class BokjiroCentralSyncServiceTest {

    @Autowired
    private BokjiroCentralSyncService bokjiroCentralSyncService;

    @Autowired
    private BokjiroCentralRepository bokjiroCentralRepository;

    @Test
    void 복지로_중앙부처_전체_데이터를_DB에_저장한다() {
        int savedCount = bokjiroCentralSyncService.syncAll(100);

        System.out.println("전체 저장/갱신 건수: " + savedCount);
        System.out.println("DB 전체 건수: " + bokjiroCentralRepository.count());

        assertThat(savedCount).isGreaterThan(0);
        assertThat(bokjiroCentralRepository.count()).isGreaterThan(0);
    }
}