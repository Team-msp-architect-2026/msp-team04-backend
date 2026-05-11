package com.moment.momentbackend.mypage.controller;

import com.moment.momentbackend.application.type.ApplicationStatus;
import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.mypage.dto.ApplicationDetailResponse;
import com.moment.momentbackend.mypage.dto.ApplicationListResponse;
import com.moment.momentbackend.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/applications")
    public ApiResponse<List<ApplicationListResponse>> getApplicationList(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) ApplicationStatus status
    ) {
        List<ApplicationListResponse> response = mypageService.getApplicationList(userId, status);
        return ApiResponse.ok(response);
    }

    @GetMapping("/applications/{applicationId}")
    public ApiResponse<ApplicationDetailResponse> getApplicationDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long applicationId
    ) {
        ApplicationDetailResponse response = mypageService.getApplicationDetail(userId, applicationId);
        return ApiResponse.ok(response);
    }
}
