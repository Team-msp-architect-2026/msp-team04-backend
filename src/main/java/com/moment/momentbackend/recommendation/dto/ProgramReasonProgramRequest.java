package com.moment.momentbackend.recommendation.dto;

import java.util.List;

public record ProgramReasonProgramRequest(
        Long programId,
        String title,
        String category,
        String description,
        String institutionName,
        String region,
        Integer price,
        Boolean isFree,
        String classType,
        Integer targetAgeMin,
        Integer targetAgeMax,
        Double ratingAvg,
        Integer reviewCount,
        List<String> tags
) {
}
