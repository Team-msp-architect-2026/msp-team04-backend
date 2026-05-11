package com.moment.momentbackend.report.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ai_report")
public class AiReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "child_id", nullable = false, unique = true)
    private Long childId;

    @Column(name = "total_support_count", nullable = false)
    private Integer totalSupportCount;

    @Column(name = "total_free_program_count", nullable = false)
    private Integer totalFreeProgramCount;

    @Column(name = "total_recommend_count", nullable = false)
    private Integer totalRecommendCount;

    @Column(name = "total_monthly_saving", nullable = false)
    private Integer totalMonthlySaving;

    @Column(name = "ai_match_score", precision = 5, scale = 2)
    private BigDecimal aiMatchScore;

    @Column(name = "summary_message", columnDefinition = "TEXT")
    private String summaryMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public AiReport(Long childId, Integer totalSupportCount, Integer totalFreeProgramCount,
                    Integer totalRecommendCount, Integer totalMonthlySaving,
                    BigDecimal aiMatchScore, String summaryMessage,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.childId = childId;
        this.totalSupportCount = totalSupportCount;
        this.totalFreeProgramCount = totalFreeProgramCount;
        this.totalRecommendCount = totalRecommendCount;
        this.totalMonthlySaving = totalMonthlySaving;
        this.aiMatchScore = aiMatchScore;
        this.summaryMessage = summaryMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(Integer totalSupportCount, Integer totalFreeProgramCount,
                       Integer totalRecommendCount, Integer totalMonthlySaving,
                       BigDecimal aiMatchScore, String summaryMessage) {
        this.totalSupportCount = totalSupportCount;
        this.totalFreeProgramCount = totalFreeProgramCount;
        this.totalRecommendCount = totalRecommendCount;
        this.totalMonthlySaving = totalMonthlySaving;
        this.aiMatchScore = aiMatchScore;
        this.summaryMessage = summaryMessage;
        this.updatedAt = LocalDateTime.now();
    }
}