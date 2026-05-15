package com.moment.momentbackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoLoginRequestDto {

    @NotBlank(message = "인가코드는 필수입니다.")
    private String code;
}