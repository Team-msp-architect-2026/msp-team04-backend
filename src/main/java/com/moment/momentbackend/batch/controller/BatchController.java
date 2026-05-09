package com.moment.momentbackend.batch.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Batch", description = "배치 수동 트리거 API")
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job syncJob;

    @Operation(summary = "공공데이터 동기화 배치 수동 실행")
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<String>> runSyncJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(syncJob, params);
            return ResponseEntity.ok(ApiResponse.ok("배치 실행 완료"));
        } catch (Exception e) {
            log.error("배치 실행 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.ok("배치 실행 실패: " + e.getMessage()));
        }
    }
}