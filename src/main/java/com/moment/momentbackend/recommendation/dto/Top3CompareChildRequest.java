package com.moment.momentbackend.recommendation.dto;

import java.util.List;

public record Top3CompareChildRequest(
        Long childId,
        Integer age,
        List<String> concerns
) {
}
