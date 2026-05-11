package com.moment.momentbackend.recommendation.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.recommendation.dto.PreferenceRequestDto;
import com.moment.momentbackend.recommendation.dto.RecommendationResponseDto;
import com.moment.momentbackend.recommendation.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.moment.momentbackend.recommendation.dto.PreferenceResponseDto;
import com.moment.momentbackend.recommendation.dto.AiRecommendationResponseDto;
import java.util.List;

import java.util.List;
import java.util.Map;

@Tag(name = "Recommendation", description = "추천 엔진 API")
@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "추천 선호도 저장")
    @PostMapping("/api/recommendation-preferences")
    public ResponseEntity<ApiResponse<Map<String, Long>>> savePreference(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PreferenceRequestDto request) {
        Long preferenceId = recommendationService.savePreference(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("preferenceId", preferenceId), "선호도 저장 완료"));
    }

    @Operation(summary = "추천 결과 조회")
    @GetMapping("/api/recommend")
    public ResponseEntity<ApiResponse<Page<RecommendationResponseDto>>> recommend(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long childId,
            @RequestParam Long preferenceId,
            @PageableDefault(size = 10, sort = "rankNo", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<RecommendationResponseDto> result = recommendationService.recommend(userId, childId, preferenceId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @Operation(summary = "추천 선호도 단건 조회")
    @GetMapping("/api/recommendation-preferences/{id}")
    public ResponseEntity<ApiResponse<PreferenceResponseDto>> getPreference(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        PreferenceResponseDto response = recommendationService.getPreference(userId, id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "자녀별 추천 결과 히스토리 조회")
    @GetMapping("/api/recommendations/history")
    public ResponseEntity<ApiResponse<List<AiRecommendationResponseDto>>> getRecommendationHistory(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long childId) {
        List<AiRecommendationResponseDto> result =
                recommendationService.getRecommendationHistory(userId, childId);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @Operation(summary = "선호도 ID 기준 추천 결과 조회")
    @GetMapping("/api/recommendations/{preferenceId}")
    public ResponseEntity<ApiResponse<List<AiRecommendationResponseDto>>> getRecommendationsByPreference(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long preferenceId) {
        List<AiRecommendationResponseDto> result =
                recommendationService.getRecommendationsByPreference(userId, preferenceId);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @Operation(summary = "선호도 ID 기준 TOP3 추천 결과 조회")
    @GetMapping("/api/recommendations/{preferenceId}/top3")
    public ResponseEntity<ApiResponse<List<AiRecommendationResponseDto>>> getTop3Recommendations(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long preferenceId) {
        List<AiRecommendationResponseDto> result =
                recommendationService.getTop3Recommendations(userId, preferenceId);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}