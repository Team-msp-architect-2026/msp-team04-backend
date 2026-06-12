package com.moment.momentbackend.benefit.controller;

import com.moment.momentbackend.benefit.dto.BenefitProfileRequestDto;
import com.moment.momentbackend.benefit.dto.BenefitProfileResponseDto;
import com.moment.momentbackend.benefit.service.BenefitProfileService;
import com.moment.momentbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Benefit Profile", description = "지원금 진단 정보 API")
@RestController
@RequestMapping("/api/benefit-profile")
@RequiredArgsConstructor
public class BenefitProfileController {

    private final BenefitProfileService benefitProfileService;

    @Operation(summary = "지원금 진단 정보 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<BenefitProfileResponseDto>> getProfile(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(benefitProfileService.getProfile(userId)));
    }

    @Operation(summary = "지원금 진단 정보 저장")
    @PutMapping
    public ResponseEntity<ApiResponse<BenefitProfileResponseDto>> saveProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody BenefitProfileRequestDto request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                benefitProfileService.saveProfile(userId, request),
                "지원금 진단 정보가 저장되었습니다"
        ));
    }
}
