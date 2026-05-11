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
@Table(
        name = "ai_recommendation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ai_recommendation_preference_program",
                        columnNames = {"preference_id", "program_id"}
                ),
                @UniqueConstraint(
                        name = "uk_ai_recommendation_preference_rank",
                        columnNames = {"preference_id", "rank_no"}
                )
        }
)
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "child_id", nullable = false)
    private Long childId;

    @Column(name = "preference_id", nullable = false)
    private Long preferenceId;

    @Column(name = "program_id", nullable = false)
    private Long programId;

    @Column(name = "total_score", nullable = false)
    private BigDecimal totalScore;

    @Column(name = "score_distance")
    private BigDecimal scoreDistance;

    @Column(name = "score_budget")
    private BigDecimal scoreBudget;

    @Column(name = "score_age")
    private BigDecimal scoreAge;

    @Column(name = "score_keyword")
    private BigDecimal scoreKeyword;

    @Column(name = "score_class_type")
    private BigDecimal scoreClassType;

    @Column(name = "score_recruiting")
    private BigDecimal scoreRecruiting;

    @Column(name = "score_review")
    private BigDecimal scoreReview;

    @Column(name = "rank_no")
    private Integer rankNo;

    @Column(name = "recommend_reason", columnDefinition = "TEXT")
    private String recommendReason;

    @Column(name = "is_top3", nullable = false)
    private Boolean isTop3;

    @Column(name = "created_at", nullable = false)
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