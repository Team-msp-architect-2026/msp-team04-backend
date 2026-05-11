package com.moment.momentbackend.recommendation.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.recommendation.dto.RecommendProgressResponseDto;
import com.moment.momentbackend.recommendation.service.RecommendProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI Recommend", description = "AI 추천 분석 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class RecommendProgressController {

    private final RecommendProgressService recommendProgressService;

    @Operation(summary = "추천 설문 진행률 조회")
    @GetMapping("/recommend/progress")
    public ResponseEntity<ApiResponse<RecommendProgressResponseDto>> getProgress(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long profileDraftId) {
        RecommendProgressResponseDto response = recommendProgressService.getProgress(userId, profileDraftId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}