package com.moment.momentbackend.auth;

import com.moment.momentbackend.auth.entity.RefreshToken;
import com.moment.momentbackend.auth.repository.RefreshTokenRepository;
import com.moment.momentbackend.auth.service.AuthService;
import com.moment.momentbackend.global.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RedisService redisService;

    @Mock
    private com.moment.momentbackend.auth.jwt.JwtTokenProvider jwtTokenProvider;

    @Mock
    private com.moment.momentbackend.auth.client.KakaoAuthClient kakaoAuthClient;

    @Mock
    private com.moment.momentbackend.user.repository.UserRepository userRepository;

    @Test
    @DisplayName("로그아웃 시 RefreshToken DB 삭제 확인")
    void logout_deleteRefreshToken() {
        Long userId = 1L;
        String accessToken = "testAccessToken";

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token("testRefreshToken")
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(refreshToken));

        authService.logout(accessToken, userId);

        verify(refreshTokenRepository, times(1)).delete(refreshToken);
        verify(redisService, times(1)).setBlacklist(accessToken, 1800000L);
    }
}