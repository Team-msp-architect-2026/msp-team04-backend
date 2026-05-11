package com.moment.momentbackend.report.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.report.dto.AiReportResponseDto;
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
@RequestMapping("/api/ai-reports")
public class AiReportController {

    private final AiReportService aiReportService;

    @Operation(summary = "AI 육아 리포트 생성/재생성")
    @PostMapping("/children/{childId}/generate")
    public ResponseEntity<ApiResponse<AiReportResponseDto>> generateReport(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long childId) {
        AiReportResponseDto response = aiReportService.generateReport(userId, childId);
        return ResponseEntity.ok(ApiResponse.ok(response, "리포트 생성 완료"));
    }

    @Operation(summary = "AI 육아 리포트 조회")
    @GetMapping("/children/{childId}")
    public ResponseEntity<ApiResponse<AiReportResponseDto>> getReport(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long childId) {
        AiReportResponseDto response = aiReportService.getReport(userId, childId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}