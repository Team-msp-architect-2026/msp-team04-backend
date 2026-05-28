package com.moment.momentbackend.publicdata.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class WelfareUnifiedSyncServiceTest {

    @Autowired
    private WelfareUnifiedSyncService syncService;

    @Test
    void 전체_정규화() {
        syncService.syncAll();
    }
}