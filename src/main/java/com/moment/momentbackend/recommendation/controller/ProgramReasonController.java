package com.moment.momentbackend.recommendation.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.recommendation.dto.ProgramReasonResponse;
import com.moment.momentbackend.recommendation.service.ProgramReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProgramReasonController {

    private final ProgramReasonService programReasonService;

    @GetMapping("/api/programs/{programId}/ai-reason")
    public ApiResponse<ProgramReasonResponse> getProgramReason(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long programId,
            @RequestParam Long preferenceId
    ) {
        return ApiResponse.ok(programReasonService.generate(userId, programId, preferenceId));
    }
}
