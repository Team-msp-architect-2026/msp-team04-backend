package com.moment.momentbackend.recommendation.entity;

import com.moment.momentbackend.recommendation.enums.*;
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

    @Enumerated(EnumType.STRING)
    @Column
    private MonthlyBudget monthlyBudget;

    @Enumerated(EnumType.STRING)
    @Column
    private TransportType transportType;

    @Enumerated(EnumType.STRING)
    @Column
    private MoveTime moveTime;

    @Enumerated(EnumType.STRING)
    @Column
    private OnlinePreference onlinePreference;

    @Enumerated(EnumType.STRING)
    @Column
    private ClassType classType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public RecommendationPreference(Long userId, Long childId, String region,
                                    MonthlyBudget monthlyBudget, TransportType transportType,
                                    MoveTime moveTime, OnlinePreference onlinePreference,
                                    ClassType classType, LocalDateTime createdAt) {
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