package com.moment.momentbackend.recommendation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PreferenceRequestDto {

    @NotNull(message = "자녀 ID는 필수입니다.")
    private Long childId;

    private String region;
    private String monthlyBudget;
    private String transportType;
    private String moveTime;
    private String onlinePreference;
    private String classType;
}