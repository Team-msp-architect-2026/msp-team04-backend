package com.moment.momentbackend.recommendation.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class ScoreBreakdownDto {
    private BigDecimal scoreDistance;
    private BigDecimal scoreBudget;
    private BigDecimal scoreAge;
    private BigDecimal scoreKeyword;
    private BigDecimal scoreClassType;
    private BigDecimal scoreRecruiting;
    private BigDecimal scoreReview;
    private BigDecimal totalScore;
}