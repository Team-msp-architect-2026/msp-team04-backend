package com.moment.momentbackend.review.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.review.dto.ReviewListResponse;
import com.moment.momentbackend.review.dto.ReviewKeywordResponse;
import com.moment.momentbackend.review.service.ReviewService;
import com.moment.momentbackend.review.service.ReviewKeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
@Tag(name = "Review", description = "프로그램 후기 API")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewKeywordService reviewKeywordService;

    @GetMapping("/{programId}/reviews")
    @Operation(summary = "프로그램 후기 목록 조회")
    public ApiResponse<List<ReviewListResponse>> getReviewList(
            @PathVariable Long programId
    ) {
        return ApiResponse.ok(reviewService.getReviewList(programId));
    }

    @GetMapping("/{programId}/reviews/ai-keywords")
    @Operation(summary = "AI 후기 키워드 분석 조회")
    public ApiResponse<ReviewKeywordResponse> getReviewKeywords(
            @PathVariable Long programId
    ) {
        return ApiResponse.ok(reviewKeywordService.generate(programId));
    }
}
