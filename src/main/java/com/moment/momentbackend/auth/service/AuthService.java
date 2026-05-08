package com.moment.momentbackend.auth.service;

import com.moment.momentbackend.auth.client.KakaoAuthClient;
import com.moment.momentbackend.auth.dto.KakaoLoginRequestDto;
import com.moment.momentbackend.auth.dto.KakaoLoginResponseDto;
import com.moment.momentbackend.auth.dto.RefreshRequestDto;
import com.moment.momentbackend.auth.dto.RefreshResponseDto;
import com.moment.momentbackend.auth.entity.RefreshToken;
import com.moment.momentbackend.auth.jwt.JwtTokenProvider;
import com.moment.momentbackend.auth.repository.RefreshTokenRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoAuthClient kakaoAuthClient;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public KakaoLoginResponseDto kakaoLogin(KakaoLoginRequestDto request) {
        // 1. 카카오 액세스 토큰 받기
        String kakaoAccessToken = kakaoAuthClient.getAccessToken(request.getCode());

        // 2. 카카오 사용자 정보 받기
        Map<String, Object> userInfo = kakaoAuthClient.getUserInfo(kakaoAccessToken);
        Long kakaoId = (Long) userInfo.get("id");

        // 3. JWT 발급
        String accessToken = jwtTokenProvider.generateAccessToken(kakaoId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(kakaoId);

        // 4. RefreshToken DB 저장 (있으면 업데이트, 없으면 새로 저장)
        Optional<RefreshToken> existing = refreshTokenRepository.findByUserId(kakaoId);
        if (existing.isPresent()) {
            existing.get().updateToken(refreshToken, LocalDateTime.now().plusDays(7));
        } else {
            refreshTokenRepository.save(RefreshToken.builder()
                    .userId(kakaoId)
                    .token(refreshToken)
                    .expiredAt(LocalDateTime.now().plusDays(7))
                    .build());
        }

        return new KakaoLoginResponseDto(accessToken, refreshToken, false);
    }

    @Transactional
    public RefreshResponseDto refresh(RefreshRequestDto request) {
        // 1. RefreshToken 검증
        Long userId = jwtTokenProvider.getUserId(request.getRefreshToken());

        // 2. DB에서 RefreshToken 유효성 검증
        RefreshToken savedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        // 3. 만료 시간 확인
        if (savedToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 4. 새 AccessToken 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);

        // 5. Refresh Token Rotation
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
        savedToken.updateToken(newRefreshToken, LocalDateTime.now().plusDays(7));

        return new RefreshResponseDto(newAccessToken);
    }
}