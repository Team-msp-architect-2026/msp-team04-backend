package com.moment.momentbackend.recommendation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "recommendation_preference")
public class RecommendationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long childId;

    @Column
    private String region;

    @Column
    private String monthlyBudget;

    @Column
    private String transportType;

    @Column
    private String moveTime;

    @Column
    private String onlinePreference;

    @Column
    private String classType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public RecommendationPreference(Long userId, Long childId, String region,
                                    String monthlyBudget, String transportType,
                                    String moveTime, String onlinePreference,
                                    String classType, LocalDateTime createdAt) {
        this.userId = userId;
        this.childId = childId;
        this.region = region;
        this.monthlyBudget = monthlyBudget;
        this.transportType = transportType;
        this.moveTime = moveTime;
        this.onlinePreference = onlinePreference;
        this.classType = classType;
        this.createdAt = createdAt;
    }
}