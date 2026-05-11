package com.moment.momentbackend.recommendation.service;

import com.moment.momentbackend.child.repository.ChildConcernRepository;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.recommendation.dto.RecommendProgressResponseDto;
import com.moment.momentbackend.recommendation.dto.RecommendProgressStepDto;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.repository.RecommendationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendProgressService {

    private final RecommendationPreferenceRepository preferenceRepository;
    private final ChildProfileRepository childProfileRepository;
    private final ChildConcernRepository childConcernRepository;

    @Transactional(readOnly = true)
    public RecommendProgressResponseDto getProgress(Long userId, Long profileDraftId) {
        // 본인 preference 확인
        RecommendationPreference preference = preferenceRepository
                .findByIdAndUserId(profileDraftId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PREFERENCE_NOT_FOUND));

        // 자녀 프로필 존재 확인
        childProfileRepository.findByIdAndUserId(preference.getChildId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_ACCESS_DENIED));

        // 관심분야 조회
        boolean hasConcern = !childConcernRepository.findByChildProfileId(preference.getChildId()).isEmpty();

        // 7단계 체크
        List<RecommendProgressStepDto> steps = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        steps.add(buildStep("지역 선택", preference.getRegion() != null && !preference.getRegion().isBlank(),
                preference.getRegion(), missing));
        steps.add(buildStep("예산 선택", preference.getMonthlyBudget() != null,
                preference.getMonthlyBudget() != null ? preference.getMonthlyBudget().name() : null, missing));
        steps.add(buildStep("이동거리 선택", preference.getMoveTime() != null,
                preference.getMoveTime() != null ? preference.getMoveTime().name() : null, missing));
        steps.add(buildStep("관심분야 선택", hasConcern, hasConcern ? "등록됨" : null, missing));
        steps.add(buildStep("수업방식 선택", preference.getClassType() != null,
                preference.getClassType() != null ? preference.getClassType().name() : null, missing));
        steps.add(buildStep("온라인 여부", preference.getOnlinePreference() != null,
                preference.getOnlinePreference() != null ? preference.getOnlinePreference().name() : null, missing));
        steps.add(buildStep("신청 가능 여부", preference.getTransportType() != null,
                preference.getTransportType() != null ? preference.getTransportType().name() : null, missing));

        int completed = (int) steps.stream().filter(RecommendProgressStepDto::isCompleted).count();
        int total = steps.size();
        int percentage = (int) Math.round((double) completed / total * 100);

        return RecommendProgressResponseDto.builder()
                .percentage(percentage)
                .completedCount(completed)
                .totalCount(total)
                .steps(steps)
                .missingConditions(missing)
                .build();
    }

    private RecommendProgressStepDto buildStep(String name, boolean completed,
                                               String value, List<String> missing) {
        if (!completed) missing.add(name);
        return RecommendProgressStepDto.builder()
                .stepName(name)
                .completed(completed)
                .value(value)
                .build();
    }
}