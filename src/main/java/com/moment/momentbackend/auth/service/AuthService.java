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
import com.moment.momentbackend.global.redis.RedisService;
import com.moment.momentbackend.user.entity.User;
import com.moment.momentbackend.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final RedisService redisService;

    @Transactional
    public KakaoLoginResponseDto kakaoLogin(KakaoLoginRequestDto request) {
        // 1. 카카오 액세스 토큰 받기
        String kakaoAccessToken = kakaoAuthClient.getAccessToken(request.getCode());

        // 2. 카카오 사용자 정보 받기
        Map<String, Object> userInfo = kakaoAuthClient.getUserInfo(kakaoAccessToken);
        String kakaoId = String.valueOf(userInfo.get("id"));

        // 3. 신규 가입 여부 확인
        Optional<User> existingUser = userRepository.findByKakaoId(kakaoId);
        boolean isNewUser = existingUser.isEmpty();

        // 4. 신규 유저면 DB 저장
        if (isNewUser) {
            Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
            String nickname = properties != null ? (String) properties.get("nickname") : "사용자";
            userRepository.save(User.builder()
                    .kakaoId(kakaoId)
                    .parentName(nickname)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        Long userId = existingUser.map(User::getId)
                .orElse(userRepository.findByKakaoId(kakaoId).get().getId());

        // 5. JWT 발급
        String accessToken = jwtTokenProvider.generateAccessToken(userId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);

        // 6. RefreshToken DB 저장
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userId);
        if (existingToken.isPresent()) {
            existingToken.get().updateToken(refreshToken, LocalDateTime.now().plusDays(7));
        } else {
            refreshTokenRepository.save(RefreshToken.builder()
                    .userId(userId)
                    .token(refreshToken)
                    .expiredAt(LocalDateTime.now().plusDays(7))
                    .build());
        }

        return new KakaoLoginResponseDto(accessToken, refreshToken, isNewUser);
    }

    @Transactional
    public RefreshResponseDto refresh(RefreshRequestDto request) {
        Long userId = jwtTokenProvider.getUserId(request.getRefreshToken());

        RefreshToken savedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        if (savedToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
        savedToken.updateToken(newRefreshToken, LocalDateTime.now().plusDays(7));

        return new RefreshResponseDto(newAccessToken);
    }

    @Transactional
    public void logout(String accessToken, Long userId) {
        // 1. DB에서 RefreshToken 삭제
        refreshTokenRepository.findByUserId(userId)
                .ifPresent(refreshTokenRepository::delete);

        // 2. AccessToken Redis 블랙리스트 등록 (30분 TTL)
        redisService.setBlacklist(accessToken, 1800000L);
    }
}