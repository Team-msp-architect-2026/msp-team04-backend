package com.moment.momentbackend.batch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BenefitCsvDto {
    private String externalSource;
    private String externalId;
    private String benefitName;
    private String benefitType;
    private Integer supportAmount;
    private String supportDescription;
    private String applyLink;
    private Integer minAge;
    private Integer maxAge;
    private String region;
    private Boolean isActive;
}