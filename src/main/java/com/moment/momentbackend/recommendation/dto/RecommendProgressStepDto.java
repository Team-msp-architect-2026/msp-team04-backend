package com.moment.momentbackend.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendProgressStepDto {

    private String stepName;
    private boolean completed;
    private String value;
}