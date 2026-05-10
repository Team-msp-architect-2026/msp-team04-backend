package com.moment.momentbackend.application.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationCreateRequest {

    @NotNull(message = "프로그램 ID는 필수입니다.")
    private Long programId;

    @NotNull(message = "자녀 ID는 필수입니다.")
    private Long childId;

    @NotBlank(message = "신청자 이름은 필수입니다.")
    private String applicantName;

    @NotBlank(message = "보호자 이름은 필수입니다.")
    private String parentName;

    @NotBlank(message = "연락처는 필수입니다.")
    private String phone;

    private String requestNote;

    private String aiStartMessage;

    @AssertTrue(message = "이용약관 동의가 필요합니다.")
    private Boolean agreeTerms;

    @AssertTrue(message = "개인정보 처리방침 동의가 필요합니다.")
    private Boolean agreePrivacy;
}