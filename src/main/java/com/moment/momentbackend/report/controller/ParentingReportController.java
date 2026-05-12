package com.moment.momentbackend.report.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.report.dto.ParentingRawReportResponseDto;
import com.moment.momentbackend.report.service.AiReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI Report", description = "AI 육아 리포트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/reports")
public class ParentingReportController {

    private final AiReportService aiReportService;

    @Operation(summary = "육아 리포트 Raw Data 조회 (홈/리포트 화면용)")
    @GetMapping("/parenting/raw")
    public ResponseEntity<ApiResponse<ParentingRawReportResponseDto>> getRawReport(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long profileId) {
        ParentingRawReportResponseDto response = aiReportService.getRawReport(userId, profileId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}