package com.moment.momentbackend.recommendation.dto;

import com.moment.momentbackend.recommendation.enums.ReasonCode;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

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
    private List<ReasonCode> reasonCodes;
}