package com.moment.momentbackend.auth.service;

import com.moment.momentbackend.auth.client.KakaoAuthClient;
import com.moment.momentbackend.auth.dto.KakaoLoginRequestDto;
import com.moment.momentbackend.auth.dto.KakaoLoginResponseDto;
import com.moment.momentbackend.auth.dto.RefreshRequestDto;
import com.moment.momentbackend.auth.dto.RefreshResponseDto;
import com.moment.momentbackend.auth.jwt.JwtTokenProvider;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoAuthClient kakaoAuthClient;

    public KakaoLoginResponseDto kakaoLogin(KakaoLoginRequestDto request) {
        // 1. 카카오 액세스 토큰 받기
        String kakaoAccessToken = kakaoAuthClient.getAccessToken(request.getCode());

        // 2. 카카오 사용자 정보 받기
        Map<String, Object> userInfo = kakaoAuthClient.getUserInfo(kakaoAccessToken);
        Long kakaoId = (Long) userInfo.get("id");

        // 3. JWT 발급 (DB 연동 전 임시로 kakaoId 사용)
        String accessToken = jwtTokenProvider.generateAccessToken(kakaoId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(kakaoId);

        return new KakaoLoginResponseDto(accessToken, refreshToken, false);
    }

    public RefreshResponseDto refresh(RefreshRequestDto request) {
        Long userId = jwtTokenProvider.getUserId(request.getRefreshToken());
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
        return new RefreshResponseDto(newAccessToken);
    }
}