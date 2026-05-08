package com.moment.momentbackend.recommendation.service;

import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.recommendation.dto.PreferenceRequestDto;
import com.moment.momentbackend.recommendation.dto.RecommendationResponseDto;
import com.moment.momentbackend.recommendation.dto.ScoreBreakdownDto;
import com.moment.momentbackend.recommendation.entity.AiRecommendation;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.repository.AiRecommendationRepository;
import com.moment.momentbackend.recommendation.repository.ProgramQueryRepository;
import com.moment.momentbackend.recommendation.repository.RecommendationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationPreferenceRepository preferenceRepository;
    private final AiRecommendationRepository aiRecommendationRepository;
    private final ChildProfileRepository childProfileRepository;
    private final ProgramQueryRepository programQueryRepository;
    private final ScoringService scoringService;

    @Transactional
    public Long savePreference(Long userId, PreferenceRequestDto request) {
        ChildProfile child = childProfileRepository.findByIdAndUserId(request.getChildId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(userId)
                .childId(child.getId())
                .region(request.getRegion())
                .monthlyBudget(request.getMonthlyBudget())
                .transportType(request.getTransportType())
                .moveTime(request.getMoveTime())
                .onlinePreference(request.getOnlinePreference())
                .classType(request.getClassType())
                .createdAt(LocalDateTime.now())
                .build();

        return preferenceRepository.save(preference).getId();
    }

    @Transactional
    public Page<RecommendationResponseDto> recommend(Long userId, Long childId,
                                                     Long preferenceId, Pageable pageable) {
        ChildProfile child = childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        RecommendationPreference preference = preferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        int childAge = Period.between(child.getBirthDate(), LocalDate.now()).getYears();

        // 기존 추천 결과 삭제 (재추천 시)
        aiRecommendationRepository.deleteAllByPreferenceId(preferenceId);

        // QueryDSL 동적 필터
        List<Program> programs = programQueryRepository.findFilteredPrograms(preference, childAge);

        // 점수 계산 및 정렬
        List<ScoredProgram> scored = new ArrayList<>();
        for (Program program : programs) {
            ScoreBreakdownDto score = scoringService.calculate(program, preference, childAge, 0.0, 0.0);
            scored.add(new ScoredProgram(program, score));
        }
        scored.sort(Comparator.comparing(s -> s.score.getTotalScore().negate()));

        // ai_recommendation 저장
        List<AiRecommendation> recommendations = new ArrayList<>();
        for (int i = 0; i < scored.size(); i++) {
            ScoredProgram sp = scored.get(i);
            int rank = i + 1;
            boolean isTop3 = rank <= 3;
            String reason = buildReason(sp.program, sp.score, isTop3);

            AiRecommendation rec = AiRecommendation.builder()
                    .userId(userId)
                    .childId(childId)
                    .preferenceId(preferenceId)
                    .programId(sp.program.getId())
                    .totalScore(sp.score.getTotalScore())
                    .scoreDistance(sp.score.getScoreDistance())
                    .scoreBudget(sp.score.getScoreBudget())
                    .scoreAge(sp.score.getScoreAge())
                    .scoreKeyword(sp.score.getScoreKeyword())
                    .scoreClassType(sp.score.getScoreClassType())
                    .scoreRecruiting(sp.score.getScoreRecruiting())
                    .scoreReview(sp.score.getScoreReview())
                    .rankNo(rank)
                    .recommendReason(reason)
                    .isTop3(isTop3)
                    .createdAt(LocalDateTime.now())
                    .build();

            recommendations.add(aiRecommendationRepository.save(rec));
        }

        // 페이지네이션 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), recommendations.size());
        List<RecommendationResponseDto> result = new ArrayList<>();

        if (start < recommendations.size()) {
            for (int i = start; i < end; i++) {
                result.add(new RecommendationResponseDto(recommendations.get(i), scored.get(i).program));
            }
        }

        return new PageImpl<>(result, pageable, recommendations.size());
    }

    private String buildReason(Program program, ScoreBreakdownDto score, boolean isTop3) {
        StringBuilder sb = new StringBuilder();
        if (isTop3) sb.append("✨ 추천 TOP3! ");
        sb.append(program.getTitle()).append("은(는) ");
        if (score.getScoreAge().doubleValue() > 15) sb.append("연령대가 딱 맞고 ");
        if (score.getScoreBudget().doubleValue() > 15) sb.append("예산 내 수업이며 ");
        if (score.getScoreReview().doubleValue() > 7) sb.append("리뷰 평점이 높습니다. ");
        sb.append("(총점: ").append(score.getTotalScore()).append("점)");
        return sb.toString();
    }

    private record ScoredProgram(Program program, ScoreBreakdownDto score) {}
}