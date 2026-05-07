package com.moment.momentbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoLoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private boolean isNewUser;
}