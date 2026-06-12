package com.moment.momentbackend.benefit.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "benefit_assessment_profile")
public class BenefitAssessmentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String region;

    @Column
    private String district;

    @Column(nullable = false)
    private Integer householdSize;

    @Column(nullable = false)
    private String monthlyIncomeRange;

    @Column(nullable = false)
    private String caregiverAgeRange;

    @Column(nullable = false)
    private Boolean dualIncome;

    @Column(nullable = false)
    private Boolean singleParent;

    @Column(nullable = false)
    private Boolean multiChildFamily;

    @Column(nullable = false)
    private Boolean multiculturalFamily;

    @Column(nullable = false)
    private Boolean disabledFamilyMember;

    @Column(nullable = false)
    private Boolean unknownIncome;

    @Column(nullable = false)
    private Boolean consentAgreed;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Builder
    public BenefitAssessmentProfile(
            Long userId,
            String region,
            String district,
            Integer householdSize,
            String monthlyIncomeRange,
            String caregiverAgeRange,
            Boolean dualIncome,
            Boolean singleParent,
            Boolean multiChildFamily,
            Boolean multiculturalFamily,
            Boolean disabledFamilyMember,
            Boolean unknownIncome,
            Boolean consentAgreed,
            LocalDateTime createdAt
    ) {
        this.userId = userId;
        this.region = region;
        this.district = district;
        this.householdSize = householdSize;
        this.monthlyIncomeRange = monthlyIncomeRange;
        this.caregiverAgeRange = caregiverAgeRange;
        this.dualIncome = dualIncome;
        this.singleParent = singleParent;
        this.multiChildFamily = multiChildFamily;
        this.multiculturalFamily = multiculturalFamily;
        this.disabledFamilyMember = disabledFamilyMember;
        this.unknownIncome = unknownIncome;
        this.consentAgreed = consentAgreed;
        this.createdAt = createdAt;
    }

    public void update(
            String region,
            String district,
            Integer householdSize,
            String monthlyIncomeRange,
            String caregiverAgeRange,
            Boolean dualIncome,
            Boolean singleParent,
            Boolean multiChildFamily,
            Boolean multiculturalFamily,
            Boolean disabledFamilyMember,
            Boolean unknownIncome,
            Boolean consentAgreed
    ) {
        this.region = region;
        this.district = district;
        this.householdSize = householdSize;
        this.monthlyIncomeRange = monthlyIncomeRange;
        this.caregiverAgeRange = caregiverAgeRange;
        this.dualIncome = dualIncome;
        this.singleParent = singleParent;
        this.multiChildFamily = multiChildFamily;
        this.multiculturalFamily = multiculturalFamily;
        this.disabledFamilyMember = disabledFamilyMember;
        this.unknownIncome = unknownIncome;
        this.consentAgreed = consentAgreed;
        this.updatedAt = LocalDateTime.now();
    }
}
