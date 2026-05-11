package com.moment.momentbackend.recommendation.dto;

import com.moment.momentbackend.recommendation.entity.AiRecommendation;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class AiRecommendationResponseDto {

    private Long recommendationId;

    private Long userId;
    private Long childId;
    private Long preferenceId;
    private Long programId;

    private BigDecimal totalScore;
    private BigDecimal scoreDistance;
    private BigDecimal scoreBudget;
    private BigDecimal scoreAge;
    private BigDecimal scoreKeyword;
    private BigDecimal scoreClassType;
    private BigDecimal scoreRecruiting;
    private BigDecimal scoreReview;

    private Integer rankNo;
    private String recommendReason;
    private Boolean isTop3;
    private LocalDateTime createdAt;

    public static AiRecommendationResponseDto from(AiRecommendation recommendation) {
        return AiRecommendationResponseDto.builder()
                .recommendationId(recommendation.getId())
                .userId(recommendation.getUserId())
                .childId(recommendation.getChildId())
                .preferenceId(recommendation.getPreferenceId())
                .programId(recommendation.getProgramId())
                .totalScore(recommendation.getTotalScore())
                .scoreDistance(recommendation.getScoreDistance())
                .scoreBudget(recommendation.getScoreBudget())
                .scoreAge(recommendation.getScoreAge())
                .scoreKeyword(recommendation.getScoreKeyword())
                .scoreClassType(recommendation.getScoreClassType())
                .scoreRecruiting(recommendation.getScoreRecruiting())
                .scoreReview(recommendation.getScoreReview())
                .rankNo(recommendation.getRankNo())
                .recommendReason(recommendation.getRecommendReason())
                .isTop3(recommendation.getIsTop3())
                .createdAt(recommendation.getCreatedAt())
                .build();
    }
}