package com.moment.momentbackend.benefit.dto;

import com.moment.momentbackend.benefit.entity.BenefitMaster;
import lombok.Getter;

@Getter
public class BenefitMasterResponseDto {

    private Long id;
    private String benefitName;
    private String benefitType;
    private Integer supportAmount;
    private String supportDescription;
    private String applyLink;
    private Integer minAge;
    private Integer maxAge;
    private String conditionDescription;
    private String region;

    public BenefitMasterResponseDto(BenefitMaster benefit) {
        this.id = benefit.getId();
        this.benefitName = benefit.getBenefitName();
        this.benefitType = benefit.getBenefitType();
        this.supportAmount = benefit.getSupportAmount();
        this.supportDescription = benefit.getSupportDescription();
        this.applyLink = benefit.getApplyLink();
        this.minAge = benefit.getMinAge();
        this.maxAge = benefit.getMaxAge();
        this.conditionDescription = benefit.getConditionDescription();
        this.region = benefit.getRegion();
    }
}