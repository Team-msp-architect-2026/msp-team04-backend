package com.moment.momentbackend.recommendation.dto;

import java.util.List;
import java.util.Map;

public record ProgramReasonScoreRequest(
        Double matchScore,
        List<String> reasonCodes,
        Map<String, Double> scoreBreakdown
) {
}
