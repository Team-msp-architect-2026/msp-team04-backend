package com.moment.momentbackend.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendProgressResponseDto {

    private int percentage;
    private int completedCount;
    private int totalCount;
    private List<RecommendProgressStepDto> steps;
    private List<String> missingConditions;
}