package com.moment.momentbackend.benefit.controller;

import com.moment.momentbackend.benefit.dto.BenefitMasterResponseDto;
import com.moment.momentbackend.benefit.dto.BenefitMatchResponseDto;
import com.moment.momentbackend.benefit.service.BenefitService;
import com.moment.momentbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Benefit", description = "정부 지원금 API")
@RestController
@RequestMapping("/api/benefits")
@RequiredArgsConstructor
public class BenefitController {

    private final BenefitService benefitService;

    @Operation(summary = "전체 지원금 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BenefitMasterResponseDto>>> getBenefits() {
        return ResponseEntity.ok(ApiResponse.ok(benefitService.getBenefits()));
    }

    @Operation(summary = "자녀 기반 지원금 매칭 실행")
    @PostMapping("/matches/recalculate")
    public ResponseEntity<ApiResponse<List<BenefitMatchResponseDto>>> recalculate(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(
                benefitService.recalculate(userId, childId), "매칭 완료"));
    }

    @Operation(summary = "저장된 매칭 결과 조회")
    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<List<BenefitMatchResponseDto>>> getMatches(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(benefitService.getMatches(userId, childId)));
    }
}