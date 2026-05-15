package com.moment.momentbackend.recommendation.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.recommendation.dto.StartDateResponseDto;
import com.moment.momentbackend.recommendation.service.StartDateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI Application", description = "AI 신청 분석 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/applications")
public class AiApplicationController {

    private final StartDateService startDateService;

    @Operation(summary = "최적 신청 시작일 조회")
    @GetMapping("/start-date")
    public ResponseEntity<ApiResponse<StartDateResponseDto>> getStartDate(
            @AuthenticationPrincipal Long userId,
            @RequestParam("programId") Long programId,
            @RequestParam("profileId") Long profileId) {
        return ResponseEntity.ok(ApiResponse.ok(
                startDateService.getStartDate(userId, programId, profileId)));
    }
}