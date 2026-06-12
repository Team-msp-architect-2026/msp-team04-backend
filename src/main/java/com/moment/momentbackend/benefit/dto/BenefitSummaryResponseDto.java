package com.moment.momentbackend.benefit.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class BenefitSummaryResponseDto {

    private final Long childId;
    private final String childName;
    private final boolean profileCompleted;
    private final int totalBenefitCount;
    private final int applicableCount;
    private final int conditionCheckCount;
    private final int estimatedMonthlySaving;
    private final String summaryMessage;
    private final String officialCheckMessage;
    private final List<BenefitMatchResponseDto> benefits;

    public BenefitSummaryResponseDto(
            Long childId,
            String childName,
            boolean profileCompleted,
            int totalBenefitCount,
            int applicableCount,
            int conditionCheckCount,
            int estimatedMonthlySaving,
            String summaryMessage,
            String officialCheckMessage,
            List<BenefitMatchResponseDto> benefits
    ) {
        this.childId = childId;
        this.childName = childName;
        this.profileCompleted = profileCompleted;
        this.totalBenefitCount = totalBenefitCount;
        this.applicableCount = applicableCount;
        this.conditionCheckCount = conditionCheckCount;
        this.estimatedMonthlySaving = estimatedMonthlySaving;
        this.summaryMessage = summaryMessage;
        this.officialCheckMessage = officialCheckMessage;
        this.benefits = benefits;
    }
}
