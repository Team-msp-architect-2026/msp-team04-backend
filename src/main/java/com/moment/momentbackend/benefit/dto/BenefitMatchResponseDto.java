package com.moment.momentbackend.benefit.dto;

import com.moment.momentbackend.benefit.entity.BenefitMatch;
import lombok.Getter;

@Getter
public class BenefitMatchResponseDto {

    private Long matchId;
    private Long benefitId;
    private String benefitName;
    private String benefitType;
    private Integer expectedMonthlySaving;
    private String matchStatus;
    private String applyLink;
    private String supportDescription;
    private String conditionDescription;
    private String region;

    public BenefitMatchResponseDto(BenefitMatch match) {
        this.matchId = match.getId();
        this.benefitId = match.getBenefit().getId();
        this.benefitName = match.getBenefit().getBenefitName();
        this.benefitType = match.getBenefit().getBenefitType();
        this.expectedMonthlySaving = match.getExpectedMonthlySaving();
        this.matchStatus = match.getMatchStatus();
        this.applyLink = match.getBenefit().getApplyLink();
        this.supportDescription = match.getBenefit().getSupportDescription();
        this.conditionDescription = match.getBenefit().getConditionDescription();
        this.region = match.getBenefit().getRegion();
    }
}
