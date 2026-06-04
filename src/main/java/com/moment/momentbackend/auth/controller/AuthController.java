package com.moment.momentbackend.auth.controller;

import com.moment.momentbackend.auth.dto.KakaoLoginRequestDto;
import com.moment.momentbackend.auth.dto.KakaoLoginResponseDto;
import com.moment.momentbackend.auth.dto.RefreshRequestDto;
import com.moment.momentbackend.auth.dto.RefreshResponseDto;
import com.moment.momentbackend.auth.service.AuthService;
import com.moment.momentbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.moment.momentbackend.auth.jwt.JwtTokenProvider;
import java.io.IOException;

import org.springframework.context.annotation.Profile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Tag(name = "Auth", description = "인증 관련 API")
@Profile("!batch")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "카카오 로그인", description = "카카오 인가코드로 JWT 발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공")
    })
    @PostMapping("/kakao")
    public ResponseEntity<ApiResponse<KakaoLoginResponseDto>> kakaoLogin(
            @Valid @RequestBody KakaoLoginRequestDto request) {
        KakaoLoginResponseDto response = authService.kakaoLogin(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/kakao")
    public void kakaoLoginRedirect(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletResponse response) throws IOException {

        KakaoLoginRequestDto request = new KakaoLoginRequestDto(code);
        KakaoLoginResponseDto tokens = authService.kakaoLogin(request);

        String appRedirectUri = (state != null && !state.isBlank())
                ? state
                : "momentapp://auth";

        String separator = appRedirectUri.contains("?") ? "&" : "?";

        String redirectUrl = appRedirectUri
                + separator
                + "accessToken=" + URLEncoder.encode(tokens.getAccessToken(), StandardCharsets.UTF_8)
                + "&refreshToken=" + URLEncoder.encode(tokens.getRefreshToken(), StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }

    @Operation(summary = "토큰 재발급", description = "refreshToken으로 새 accessToken 발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponseDto>> refresh(
            @Valid @RequestBody RefreshRequestDto request) {
        RefreshResponseDto response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "로그아웃", description = "RefreshToken 삭제 및 AccessToken 블랙리스트 등록")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String token = resolveToken(request);
        Long userId = jwtTokenProvider.getUserId(token);
        authService.logout(token, userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "로그아웃 완료"));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}