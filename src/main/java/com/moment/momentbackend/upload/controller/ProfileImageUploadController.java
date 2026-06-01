package com.moment.momentbackend.upload.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.upload.dto.ProfileImagePresignedUrlResponse;
import com.moment.momentbackend.upload.service.ProfileImageUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Upload", description = "프로필 이미지 업로드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uploads/profile")
public class ProfileImageUploadController {

    private final ProfileImageUploadService profileImageUploadService;

    @Operation(summary = "프로필 이미지 업로드 Presigned URL 발급")
    @GetMapping("/presigned-url")
    public ApiResponse<ProfileImagePresignedUrlResponse> getProfileImagePresignedUrl(
            @AuthenticationPrincipal Long userId,
            @RequestParam String contentType
    ) {
        return ApiResponse.ok(profileImageUploadService.createPresignedUrl(userId, contentType));
    }
}
