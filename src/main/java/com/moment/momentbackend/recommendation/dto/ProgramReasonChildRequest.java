package com.moment.momentbackend.recommendation.dto;

import java.util.List;

public record ProgramReasonChildRequest(
        Long childId,
        Integer age,
        List<String> concerns
) {
}
