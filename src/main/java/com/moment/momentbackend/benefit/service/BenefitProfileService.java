package com.moment.momentbackend.benefit.service;

import com.moment.momentbackend.benefit.dto.BenefitProfileRequestDto;
import com.moment.momentbackend.benefit.dto.BenefitProfileResponseDto;
import com.moment.momentbackend.benefit.entity.BenefitAssessmentProfile;
import com.moment.momentbackend.benefit.repository.BenefitAssessmentProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BenefitProfileService {

    private static final Set<String> MONTHLY_INCOME_RANGES = Set.of(
            "UNKNOWN",
            "UNDER_200",
            "RANGE_200_350",
            "RANGE_350_500",
            "RANGE_500_700",
            "OVER_700"
    );

    private static final Set<String> CAREGIVER_AGE_RANGES = Set.of(
            "UNKNOWN",
            "UNDER_30",
            "RANGE_30_39",
            "RANGE_40_49",
            "RANGE_50_59",
            "OVER_60"
    );

    private final BenefitAssessmentProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public BenefitProfileResponseDto getProfile(Long userId) {
        validateUserId(userId);

        return profileRepository.findByUserId(userId)
                .map(BenefitProfileResponseDto::from)
                .orElseGet(BenefitProfileResponseDto::empty);
    }

    @Transactional
    public BenefitProfileResponseDto saveProfile(Long userId, BenefitProfileRequestDto request) {
        validateUserId(userId);
        validateRequest(request);

        String monthlyIncomeRange = normalize(request.getMonthlyIncomeRange());
        String caregiverAgeRange = normalize(request.getCaregiverAgeRange());
        Boolean unknownIncome = bool(request.getUnknownIncome()) || "UNKNOWN".equals(monthlyIncomeRange);

        BenefitAssessmentProfile profile = profileRepository.findByUserId(userId)
                .map(existing -> {
                    existing.update(
                            clean(request.getRegion()),
                            cleanNullable(request.getDistrict()),
                            request.getHouseholdSize(),
                            monthlyIncomeRange,
                            caregiverAgeRange,
                            bool(request.getDualIncome()),
                            bool(request.getSingleParent()),
                            bool(request.getMultiChildFamily()),
                            bool(request.getMulticulturalFamily()),
                            bool(request.getDisabledFamilyMember()),
                            unknownIncome,
                            true
                    );
                    return existing;
                })
                .orElseGet(() -> BenefitAssessmentProfile.builder()
                        .userId(userId)
                        .region(clean(request.getRegion()))
                        .district(cleanNullable(request.getDistrict()))
                        .householdSize(request.getHouseholdSize())
                        .monthlyIncomeRange(monthlyIncomeRange)
                        .caregiverAgeRange(caregiverAgeRange)
                        .dualIncome(bool(request.getDualIncome()))
                        .singleParent(bool(request.getSingleParent()))
                        .multiChildFamily(bool(request.getMultiChildFamily()))
                        .multiculturalFamily(bool(request.getMulticulturalFamily()))
                        .disabledFamilyMember(bool(request.getDisabledFamilyMember()))
                        .unknownIncome(unknownIncome)
                        .consentAgreed(true)
                        .createdAt(LocalDateTime.now())
                        .build());

        return BenefitProfileResponseDto.from(profileRepository.save(profile));
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void validateRequest(BenefitProfileRequestDto request) {
        if (request == null) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        if (isBlank(request.getRegion())) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        if (request.getHouseholdSize() == null
                || request.getHouseholdSize() < 1
                || request.getHouseholdSize() > 20) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        String monthlyIncomeRange = normalize(request.getMonthlyIncomeRange());
        if (!MONTHLY_INCOME_RANGES.contains(monthlyIncomeRange)) {
            throw new CustomException(ErrorCode.INVALID_ENUM_VALUE);
        }

        String caregiverAgeRange = normalize(request.getCaregiverAgeRange());
        if (!CAREGIVER_AGE_RANGES.contains(caregiverAgeRange)) {
            throw new CustomException(ErrorCode.INVALID_ENUM_VALUE);
        }

        if (!Boolean.TRUE.equals(request.getConsentAgreed())) {
            throw new CustomException(ErrorCode.INVALID_AGREEMENT);
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toUpperCase();
    }

    private String clean(String value) {
        if (isBlank(value)) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }
        return value.trim();
    }

    private String cleanNullable(String value) {
        if (isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Boolean bool(Boolean value) {
        return Boolean.TRUE.equals(value);
    }
}
