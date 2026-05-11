package com.moment.momentbackend.report.service;

import com.moment.momentbackend.benefit.entity.BenefitMatch;
import com.moment.momentbackend.benefit.repository.BenefitMatchRepository;
import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.recommendation.repository.AiRecommendationRepository;
import com.moment.momentbackend.report.dto.AiReportResponseDto;
import com.moment.momentbackend.report.entity.AiReport;
import com.moment.momentbackend.report.repository.AiReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiReportServiceTest {

    @InjectMocks
    private AiReportService aiReportService;

    @Mock private AiReportRepository aiReportRepository;
    @Mock private ChildProfileRepository childProfileRepository;
    @Mock private BenefitMatchRepository benefitMatchRepository;
    @Mock private AiRecommendationRepository aiRecommendationRepository;
    @Mock private ProgramRepository programRepository;

    @Test
    @DisplayName("리포트 신규 생성 - 정상 케이스")
    void generateReport_newReport_success() {
        Long userId = 1L, childId = 1L;

        given(childProfileRepository.findByIdAndUserId(childId, userId))
                .willReturn(Optional.of(mock(ChildProfile.class)));

        BenefitMatch match = mock(BenefitMatch.class);
        given(match.getMatchStatus()).willReturn("APPLICABLE");
        given(match.getExpectedMonthlySaving()).willReturn(50000);
        given(benefitMatchRepository.findAllByChildIdWithBenefit(childId))
                .willReturn(List.of(match));

        given(programRepository.countByIsFreeTrueAndIsPublicTrue()).willReturn(5L);
        given(aiRecommendationRepository.countByChildId(childId)).willReturn(3L);
        given(aiReportRepository.findByChildId(childId)).willReturn(Optional.empty());

        AiReport saved = AiReport.builder()
                .childId(childId)
                .totalSupportCount(1)
                .totalFreeProgramCount(5)
                .totalRecommendCount(3)
                .totalMonthlySaving(50000)
                .aiMatchScore(BigDecimal.valueOf(50.0))
                .summaryMessage("테스트 메시지")
                .createdAt(LocalDateTime.now())
                .build();
        given(aiReportRepository.save(any())).willReturn(saved);

        AiReportResponseDto result = aiReportService.generateReport(userId, childId);

        assertThat(result).isNotNull();
        assertThat(result.getTotalSupportCount()).isEqualTo(1);
        assertThat(result.getTotalMonthlySaving()).isEqualTo(50000);
        verify(aiReportRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("리포트 재생성 - 기존 리포트 덮어쓰기 확인")
    void generateReport_existingReport_updated() {
        Long userId = 1L, childId = 1L;

        given(childProfileRepository.findByIdAndUserId(childId, userId))
                .willReturn(Optional.of(mock(ChildProfile.class)));
        given(benefitMatchRepository.findAllByChildIdWithBenefit(childId))
                .willReturn(List.of());
        given(programRepository.countByIsFreeTrueAndIsPublicTrue()).willReturn(0L);
        given(aiRecommendationRepository.countByChildId(childId)).willReturn(0L);

        AiReport existing = spy(AiReport.builder()
                .childId(childId)
                .totalSupportCount(0)
                .totalFreeProgramCount(0)
                .totalRecommendCount(0)
                .totalMonthlySaving(0)
                .aiMatchScore(BigDecimal.valueOf(20.0))
                .summaryMessage("기존 메시지")
                .createdAt(LocalDateTime.now())
                .build());

        given(aiReportRepository.findByChildId(childId)).willReturn(Optional.of(existing));
        given(aiReportRepository.save(any())).willReturn(existing);

        aiReportService.generateReport(userId, childId);

        verify(existing, times(1)).update(any(), any(), any(), any(), any(), any());
        verify(aiReportRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("리포트 조회 - 정상 케이스")
    void getReport_success() {
        Long userId = 1L, childId = 1L;

        given(childProfileRepository.findByIdAndUserId(childId, userId))
                .willReturn(Optional.of(mock(ChildProfile.class)));

        AiReport report = AiReport.builder()
                .childId(childId)
                .totalSupportCount(2)
                .totalFreeProgramCount(3)
                .totalRecommendCount(5)
                .totalMonthlySaving(80000)
                .aiMatchScore(BigDecimal.valueOf(70.0))
                .summaryMessage("요약 메시지")
                .createdAt(LocalDateTime.now())
                .build();

        given(aiReportRepository.findByChildId(childId)).willReturn(Optional.of(report));

        AiReportResponseDto result = aiReportService.getReport(userId, childId);

        assertThat(result.getChildId()).isEqualTo(childId);
        assertThat(result.getTotalSupportCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("리포트 조회 - 타인 자녀 접근 시 예외")
    void getReport_childNotOwned_throwsException() {
        given(childProfileRepository.findByIdAndUserId(1L, 99L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> aiReportService.getReport(99L, 1L))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("매칭 점수 - 혜택 0건이면 기본 20점")
    void matchScore_noSupport_returns20() {
        Long userId = 1L, childId = 1L;

        given(childProfileRepository.findByIdAndUserId(childId, userId))
                .willReturn(Optional.of(mock(ChildProfile.class)));
        given(benefitMatchRepository.findAllByChildIdWithBenefit(childId))
                .willReturn(List.of());
        given(programRepository.countByIsFreeTrueAndIsPublicTrue()).willReturn(0L);
        given(aiRecommendationRepository.countByChildId(childId)).willReturn(0L);
        given(aiReportRepository.findByChildId(childId)).willReturn(Optional.empty());

        AiReport saved = AiReport.builder()
                .childId(childId)
                .totalSupportCount(0).totalFreeProgramCount(0)
                .totalRecommendCount(0).totalMonthlySaving(0)
                .aiMatchScore(BigDecimal.valueOf(20.0))
                .summaryMessage("").createdAt(LocalDateTime.now())
                .build();
        given(aiReportRepository.save(any())).willReturn(saved);

        AiReportResponseDto result = aiReportService.generateReport(userId, childId);

        assertThat(result.getAiMatchScore()).isEqualByComparingTo(BigDecimal.valueOf(20.0));
    }
}