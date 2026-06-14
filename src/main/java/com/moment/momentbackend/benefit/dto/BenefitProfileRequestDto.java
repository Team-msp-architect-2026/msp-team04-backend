package com.moment.momentbackend.benefit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BenefitProfileRequestDto {

    private String region;
    private String district;
    private Integer householdSize;
    private String monthlyIncomeRange;
    private String caregiverAgeRange;
    private Boolean dualIncome;
    private Boolean singleParent;
    private Boolean multiChildFamily;
    private Boolean multiculturalFamily;
    private Boolean disabledFamilyMember;
    private Boolean unknownIncome;
    private Boolean consentAgreed;
}
