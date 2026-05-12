package com.moment.momentbackend.recommendation.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.recommendation.dto.NextRecommendResponseDto;
import com.moment.momentbackend.recommendation.service.NextRecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI Recommend", description = "AI 다음 추천 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/recommend")
public class AiNextRecommendController {

    private final NextRecommendService nextRecommendService;

    @Operation(summary = "신청 완료 후 다음 추천 후보 조회")
    @GetMapping("/next")
    public ResponseEntity<ApiResponse<NextRecommendResponseDto>> getNextRecommend(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long reservationId) {
        return ResponseEntity.ok(ApiResponse.ok(
                nextRecommendService.getNextRecommend(userId, reservationId)));
    }
}