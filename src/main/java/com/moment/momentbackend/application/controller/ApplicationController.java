package com.moment.momentbackend.application.controller;

import com.moment.momentbackend.application.dto.ApplicationAvailabilityResponse;
import com.moment.momentbackend.application.dto.ApplicationCreateRequest;
import com.moment.momentbackend.application.dto.ApplicationCreateResponse;
import com.moment.momentbackend.application.service.ApplicationService;
import com.moment.momentbackend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/availability/{programId}")
    public ApiResponse<ApplicationAvailabilityResponse> getAvailability(
            @PathVariable Long programId
    ) {
        ApplicationAvailabilityResponse response = applicationService.getAvailability(programId);
        return ApiResponse.ok(response);
    }

    @PostMapping
    public ApiResponse<ApplicationCreateResponse> createApplication(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ApplicationCreateRequest request
    ) {
        ApplicationCreateResponse response = applicationService.createApplication(userId, request);
        return ApiResponse.ok(response, "신청이 생성되었습니다.");
    }
}