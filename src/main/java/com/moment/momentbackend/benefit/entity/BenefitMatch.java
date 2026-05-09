package com.moment.momentbackend.benefit.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "benefit_match")
public class BenefitMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long childId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_id", nullable = false)
    private BenefitMaster benefit;

    @Column(nullable = false)
    private String matchStatus;

    @Column
    private Integer expectedMonthlySaving;

    @Column(nullable = false)
    private LocalDateTime matchedAt;

    @Builder
    public BenefitMatch(Long userId, Long childId, BenefitMaster benefit,
                        String matchStatus, Integer expectedMonthlySaving,
                        LocalDateTime matchedAt) {
        this.userId = userId;
        this.childId = childId;
        this.benefit = benefit;
        this.matchStatus = matchStatus;
        this.expectedMonthlySaving = expectedMonthlySaving;
        this.matchedAt = matchedAt;
    }
}