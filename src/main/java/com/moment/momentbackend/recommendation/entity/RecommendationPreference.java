package com.moment.momentbackend.recommendation.entity;

import com.moment.momentbackend.recommendation.enums.*;
import com.moment.momentbackend.recommendation.converter.MonthlyBudgetConverter;
import com.moment.momentbackend.recommendation.converter.MoveTimeConverter;
import com.moment.momentbackend.recommendation.converter.OnlinePreferenceConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @Convert(converter = MonthlyBudgetConverter.class)
    @Column
    private MonthlyBudget monthlyBudget;

    @Enumerated(EnumType.STRING)
    @Column
    private TransportType transportType;

    @Convert(converter = MoveTimeConverter.class)
    @Column
    private MoveTime moveTime;

    @Convert(converter = OnlinePreferenceConverter.class)
    @Column
    private OnlinePreference onlinePreference;

    @Enumerated(EnumType.STRING)
    @Column
    private ClassType classType;

    @ElementCollection
    @CollectionTable(
            name = "recommendation_preference_concerns",
            joinColumns = @JoinColumn(name = "preference_id")
    )
    @Column(name = "concern")
    private List<String> concerns = List.of();

    @ElementCollection
    @CollectionTable(
            name = "recommendation_preference_subject_details",
            joinColumns = @JoinColumn(name = "preference_id")
    )
    @Column(name = "subject_detail")
    private List<String> subjectDetails = List.of();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public RecommendationPreference(Long userId, Long childId, String region,
                                    MonthlyBudget monthlyBudget, TransportType transportType,
                                    MoveTime moveTime, OnlinePreference onlinePreference,
                                    ClassType classType,
                                    List<String> concerns,
                                    List<String> subjectDetails,
                                    LocalDateTime createdAt) {
        this.userId = userId;
        this.childId = childId;
        this.region = region;
        this.monthlyBudget = monthlyBudget;
        this.transportType = transportType;
        this.moveTime = moveTime;
        this.onlinePreference = onlinePreference;
        this.classType = classType;
        this.concerns = concerns == null ? List.of() : concerns;
        this.subjectDetails = subjectDetails == null ? List.of() : subjectDetails;
        this.createdAt = createdAt;
    }
}