package com.moment.momentbackend.recommendation.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.recommendation.dto.Top3CompareResponse;
import com.moment.momentbackend.recommendation.service.Top3CompareService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiTop3CompareController {

    private final Top3CompareService top3CompareService;

    @PostMapping("/api/ai/recommend/top3/compare")
    public ApiResponse<Top3CompareResponse> compareTop3(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long preferenceId
    ) {
        return ApiResponse.ok(top3CompareService.compare(userId, preferenceId));
    }
}
