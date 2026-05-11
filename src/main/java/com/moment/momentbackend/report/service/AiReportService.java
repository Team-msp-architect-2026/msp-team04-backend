package com.moment.momentbackend.report.service;

import com.moment.momentbackend.benefit.entity.BenefitMatch;
import com.moment.momentbackend.benefit.repository.BenefitMatchRepository;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.recommendation.repository.AiRecommendationRepository;
import com.moment.momentbackend.report.dto.AiReportResponseDto;
import com.moment.momentbackend.report.entity.AiReport;
import com.moment.momentbackend.report.repository.AiReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiReportService {

    private final AiReportRepository aiReportRepository;
    private final ChildProfileRepository childProfileRepository;
    private final BenefitMatchRepository benefitMatchRepository;
    private final AiRecommendationRepository aiRecommendationRepository;
    private final ProgramRepository programRepository;

    @Transactional
    public AiReportResponseDto generateReport(Long userId, Long childId) {
        // 본인 자녀 소유권 확인
        childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_ACCESS_DENIED));

        // 리포트 데이터 수집
        List<BenefitMatch> matches = benefitMatchRepository.findAllByChildIdWithBenefit(childId);

        int totalSupportCount = (int) matches.stream()
                .filter(m -> "APPLICABLE".equals(m.getMatchStatus()))
                .count();

        int totalMonthlySaving = matches.stream()
                .filter(m -> "APPLICABLE".equals(m.getMatchStatus()))
                .mapToInt(m -> m.getExpectedMonthlySaving() != null ? m.getExpectedMonthlySaving() : 0)
                .sum();

        int totalFreeProgramCount = (int) programRepository.countByIsFreeTrueAndIsPublicTrue();
        int totalRecommendCount = (int) aiRecommendationRepository.countByChildId(childId);

        BigDecimal aiMatchScore = calcMatchScore(totalSupportCount, totalMonthlySaving, totalRecommendCount);
        String summaryMessage = buildSummaryMessage(totalSupportCount, totalMonthlySaving,
                totalFreeProgramCount, totalRecommendCount, aiMatchScore);

        // 기존 리포트 있으면 덮어쓰기, 없으면 신규 저장
        Optional<AiReport> existing = aiReportRepository.findByChildId(childId);

        AiReport report;
        if (existing.isPresent()) {
            report = existing.get();
            report.update(totalSupportCount, totalFreeProgramCount, totalRecommendCount,
                    totalMonthlySaving, aiMatchScore, summaryMessage);
        } else {
            report = AiReport.builder()
                    .childId(childId)
                    .totalSupportCount(totalSupportCount)
                    .totalFreeProgramCount(totalFreeProgramCount)
                    .totalRecommendCount(totalRecommendCount)
                    .totalMonthlySaving(totalMonthlySaving)
                    .aiMatchScore(aiMatchScore)
                    .summaryMessage(summaryMessage)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        return AiReportResponseDto.from(aiReportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public AiReportResponseDto getReport(Long userId, Long childId) {
        childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_ACCESS_DENIED));

        AiReport report = aiReportRepository.findByChildId(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return AiReportResponseDto.from(report);
    }

    // rule-based 점수 계산
    private BigDecimal calcMatchScore(int supportCount, int monthlySaving, int recommendCount) {
        double score = 20.0;
        if (supportCount >= 3) score += 30.0;
        else if (supportCount >= 1) score += 15.0;

        if (monthlySaving >= 100000) score += 20.0;
        else if (monthlySaving >= 50000) score += 10.0;

        if (recommendCount >= 5) score += 20.0;
        else if (recommendCount >= 1) score += 10.0;

        score = Math.min(score, 100.0);
        return BigDecimal.valueOf(score);
    }

    // rule-based 요약 메시지 생성
    private String buildSummaryMessage(int supportCount, int monthlySaving,
                                       int freeProgramCount, int recommendCount,
                                       BigDecimal matchScore) {
        StringBuilder sb = new StringBuilder();
        sb.append("현재 자녀에게 적합한 지원 혜택이 ").append(supportCount).append("건 확인되었습니다. ");

        if (monthlySaving > 0) {
            sb.append("월 최대 ").append(String.format("%,d", monthlySaving))
                    .append("원의 절감이 가능합니다. ");
        }

        if (freeProgramCount > 0) {
            sb.append("현재 무료 프로그램 ").append(freeProgramCount).append("개가 운영 중입니다. ");
        }

        if (recommendCount > 0) {
            sb.append("추천된 프로그램 ").append(recommendCount).append("개를 확인해보세요. ");
        }

        sb.append("(종합 매칭 점수: ").append(matchScore).append("점)");
        return sb.toString();
    }
}