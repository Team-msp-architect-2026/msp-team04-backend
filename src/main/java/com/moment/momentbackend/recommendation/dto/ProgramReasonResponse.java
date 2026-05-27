package com.moment.momentbackend.recommendation.dto;

import java.util.List;

public record ProgramReasonResponse(
        Double matchScore,
        List<String> reasonList,
        String source
) {
}
