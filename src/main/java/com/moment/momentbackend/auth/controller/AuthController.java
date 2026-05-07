package com.moment.momentbackend.auth.controller;

import com.moment.momentbackend.auth.dto.RefreshRequestDto;
import com.moment.momentbackend.auth.dto.RefreshResponseDto;
import com.moment.momentbackend.auth.service.AuthService;
import com.moment.momentbackend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponseDto>> refresh(
            @Valid @RequestBody RefreshRequestDto request) {
        RefreshResponseDto response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}