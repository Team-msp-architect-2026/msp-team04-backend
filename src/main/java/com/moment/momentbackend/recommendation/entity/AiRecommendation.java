package com.moment.momentbackend.recommendation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ai_recommendation")
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long childId;

    @Column(nullable = false)
    private Long preferenceId;

    @Column(nullable = false)
    private Long programId;

    @Column(nullable = false)
    private BigDecimal totalScore;

    @Column
    private BigDecimal scoreDistance;

    @Column
    private BigDecimal scoreBudget;

    @Column
    private BigDecimal scoreAge;

    @Column
    private BigDecimal scoreKeyword;

    @Column
    private BigDecimal scoreClassType;

    @Column
    private BigDecimal scoreRecruiting;

    @Column
    private BigDecimal scoreReview;

    @Column
    private Integer rankNo;

    @Column(columnDefinition = "TEXT")
    private String recommendReason;

    @Column(nullable = false)
    private Boolean isTop3;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public AiRecommendation(Long userId, Long childId, Long preferenceId, Long programId,
                            BigDecimal totalScore, BigDecimal scoreDistance, BigDecimal scoreBudget,
                            BigDecimal scoreAge, BigDecimal scoreKeyword, BigDecimal scoreClassType,
                            BigDecimal scoreRecruiting, BigDecimal scoreReview,
                            Integer rankNo, String recommendReason, Boolean isTop3,
                            LocalDateTime createdAt) {
        this.userId = userId;
        this.childId = childId;
        this.preferenceId = preferenceId;
        this.programId = programId;
        this.totalScore = totalScore;
        this.scoreDistance = scoreDistance;
        this.scoreBudget = scoreBudget;
        this.scoreAge = scoreAge;
        this.scoreKeyword = scoreKeyword;
        this.scoreClassType = scoreClassType;
        this.scoreRecruiting = scoreRecruiting;
        this.scoreReview = scoreReview;
        this.rankNo = rankNo;
        this.recommendReason = recommendReason;
        this.isTop3 = isTop3;
        this.createdAt = createdAt;
    }
}