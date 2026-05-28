package com.moment.momentbackend.benefit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "benefit_master")
public class BenefitMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String benefitName;

    @Column
    private String benefitType;

    @Column
    private Integer supportAmount;

    @Column
    private String supportDescription;

    @Column
    private String applyLink;

    @Column
    private Integer minAge;

    @Column
    private Integer maxAge;

    @Column(columnDefinition = "TEXT")
    private String conditionDescription;

    @Column
    private String region;

    @Column(nullable = false)
    private Boolean isActive;

    @Column
    private String externalSource;

    @Column
    private String externalId;

    @Column
    private LocalDateTime lastSyncedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}