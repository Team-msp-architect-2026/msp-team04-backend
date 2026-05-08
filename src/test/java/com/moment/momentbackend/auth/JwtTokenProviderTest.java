package com.moment.momentbackend.auth;

import com.moment.momentbackend.auth.jwt.JwtTokenProvider;
import com.moment.momentbackend.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "bW9tZW50c2VjcmV0a2V5Zm9ybG9jYWxkZXZlbG9wbWVudDEyMzQ1Njc4OTA=";
        long expiry = 1800000L;
        jwtTokenProvider = new JwtTokenProvider(secret, expiry);
    }

    @Test
    @DisplayName("Access Token 발급 성공")
    void generateAccessToken_success() {
        Long userId = 1L;
        String token = jwtTokenProvider.generateAccessToken(userId);
        assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("Refresh Token 발급 성공")
    void generateRefreshToken_success() {
        Long userId = 1L;
        String token = jwtTokenProvider.generateRefreshToken(userId);
        assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("토큰에서 userId 추출 성공")
    void getUserId_success() {
        Long userId = 1L;
        String token = jwtTokenProvider.generateAccessToken(userId);
        Long extractedId = jwtTokenProvider.getUserId(token);
        assertThat(extractedId).isEqualTo(userId);
    }

    @Test
    @DisplayName("변조된 토큰 검증 실패 - 401")
    void invalidToken_throwsException() {
        assertThatThrownBy(() -> jwtTokenProvider.getUserId("invalidtoken123"))
                .isInstanceOf(CustomException.class);
    }
}