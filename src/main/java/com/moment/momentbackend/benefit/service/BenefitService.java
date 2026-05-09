package com.moment.momentbackend.benefit.service;

import com.moment.momentbackend.benefit.dto.BenefitMasterResponseDto;
import com.moment.momentbackend.benefit.dto.BenefitMatchResponseDto;
import com.moment.momentbackend.benefit.entity.BenefitMaster;
import com.moment.momentbackend.benefit.entity.BenefitMatch;
import com.moment.momentbackend.benefit.repository.BenefitMasterRepository;
import com.moment.momentbackend.benefit.repository.BenefitMatchRepository;
import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BenefitService {

    private final BenefitMasterRepository benefitMasterRepository;
    private final BenefitMatchRepository benefitMatchRepository;
    private final ChildProfileRepository childProfileRepository;

    // 전체 지원금 목록 조회
    @Transactional(readOnly = true)
    public List<BenefitMasterResponseDto> getBenefits() {
        return benefitMasterRepository.findAllByIsActiveTrue().stream()
                .map(BenefitMasterResponseDto::new)
                .collect(Collectors.toList());
    }

    // 자녀 기반 매칭 실행 및 저장
    @Transactional
    public List<BenefitMatchResponseDto> recalculate(Long userId, Long childId) {
        ChildProfile child = childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        int childAge = Period.between(child.getBirthDate(), LocalDate.now()).getYears();

        // 기존 매칭 삭제
        benefitMatchRepository.deleteAllByChildId(childId);

        // 활성 지원금 전체 매칭
        List<BenefitMaster> benefits = benefitMasterRepository.findAllByIsActiveTrue();

        List<BenefitMatch> matches = benefits.stream().map(benefit -> {
            String status = isEligible(benefit, childAge, child) ? "APPLICABLE" : "NOT_ELIGIBLE";
            Integer saving = "APPLICABLE".equals(status) ? benefit.getSupportAmount() : null;

            return BenefitMatch.builder()
                    .userId(userId)
                    .childId(childId)
                    .benefit(benefit)
                    .matchStatus(status)
                    .expectedMonthlySaving(saving)
                    .matchedAt(LocalDateTime.now())
                    .build();
        }).collect(Collectors.toList());

        benefitMatchRepository.saveAll(matches);

        return matches.stream()
                .map(BenefitMatchResponseDto::new)
                .collect(Collectors.toList());
    }

    // 저장된 매칭 결과 조회
    @Transactional(readOnly = true)
    public List<BenefitMatchResponseDto> getMatches(Long userId, Long childId) {
        childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return benefitMatchRepository.findAllByChildIdWithBenefit(childId).stream()
                .map(BenefitMatchResponseDto::new)
                .collect(Collectors.toList());
    }

    // 자격 조건 매칭 로직
    public boolean isEligible(BenefitMaster benefit, int childAge, ChildProfile child) {
        // is_active 체크
        if (!benefit.getIsActive()) return false;

        // 나이 범위 체크
        if (benefit.getMinAge() != null && childAge < benefit.getMinAge()) return false;
        if (benefit.getMaxAge() != null && childAge > benefit.getMaxAge()) return false;

        // 지역 체크 (benefit 지역이 null이면 전국)
        if (benefit.getRegion() != null && !benefit.getRegion().isBlank()) {
            if (child.getBirthDate() == null) return false;
            // 자녀 프로필에 지역 정보가 없으므로 지역 조건 있으면 APPLICABLE로 처리
            // 실제 운영 시 user 주소와 비교 필요
        }

        return true;
    }
}