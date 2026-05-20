package com.moment.momentbackend.recommendation.dto;

public record ProgramReasonPreferenceRequest(
        Long preferenceId,
        String region,
        String monthlyBudget,
        String transportType,
        String moveTime,
        String onlinePreference,
        String classType
) {
}
