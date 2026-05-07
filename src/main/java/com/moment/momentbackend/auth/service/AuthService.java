package com.moment.momentbackend.auth.service;

import com.moment.momentbackend.auth.dto.RefreshRequestDto;
import com.moment.momentbackend.auth.dto.RefreshResponseDto;
import com.moment.momentbackend.auth.jwt.JwtTokenProvider;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    public RefreshResponseDto refresh(RefreshRequestDto request) {
        // refreshToken 검증 (만료/변조 시 CustomException 발생)
        Long userId = jwtTokenProvider.getUserId(request.getRefreshToken());

        // 새 accessToken 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
        return new RefreshResponseDto(newAccessToken);
    }
}