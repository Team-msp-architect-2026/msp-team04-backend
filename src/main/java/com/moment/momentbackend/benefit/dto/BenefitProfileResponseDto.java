package com.moment.momentbackend.benefit.dto;

import com.moment.momentbackend.benefit.entity.BenefitAssessmentProfile;
import lombok.Getter;

@Getter
public class BenefitProfileResponseDto {

    private final boolean completed;
    private final Long profileId;
    private final String region;
    private final String district;
    private final Integer householdSize;
    private final String monthlyIncomeRange;
    private final String caregiverAgeRange;
    private final Boolean dualIncome;
    private final Boolean singleParent;
    private final Boolean multiChildFamily;
    private final Boolean multiculturalFamily;
    private final Boolean disabledFamilyMember;
    private final Boolean unknownIncome;
    private final Boolean consentAgreed;

    private BenefitProfileResponseDto(
            boolean completed,
            Long profileId,
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
        this.completed = completed;
        this.profileId = profileId;
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
    }

    public static BenefitProfileResponseDto empty() {
        return new BenefitProfileResponseDto(
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                false,
                false,
                false,
                false,
                false
        );
    }

    public static BenefitProfileResponseDto from(BenefitAssessmentProfile profile) {
        return new BenefitProfileResponseDto(
                true,
                profile.getId(),
                profile.getRegion(),
                profile.getDistrict(),
                profile.getHouseholdSize(),
                profile.getMonthlyIncomeRange(),
                profile.getCaregiverAgeRange(),
                profile.getDualIncome(),
                profile.getSingleParent(),
                profile.getMultiChildFamily(),
                profile.getMulticulturalFamily(),
                profile.getDisabledFamilyMember(),
                profile.getUnknownIncome(),
                profile.getConsentAgreed()
        );
    }
}
