package com.moment.momentbackend.recommendation.dto;

public record Top3ComparePreferenceRequest(
        Long preferenceId,
        String region,
        String monthlyBudget,
        String transportType,
        String moveTime,
        String onlinePreference,
        String classType
) {
}
