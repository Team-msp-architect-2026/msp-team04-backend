package com.moment.momentbackend.recommendation;

import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.recommendation.dto.ScoreBreakdownDto;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.enums.*;
import com.moment.momentbackend.recommendation.service.ScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScoringServiceTest {

    private ScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new ScoringService();
    }

    @Test
    @DisplayName("총점은 100점을 초과하지 않는다")
    void totalScore_doesNotExceed100() {
        Program program = mock(Program.class);
        when(program.getLatitude()).thenReturn(new BigDecimal("37.5"));
        when(program.getLongitude()).thenReturn(new BigDecimal("127.0"));
        when(program.getPrice()).thenReturn(0);
        when(program.getIsFree()).thenReturn(true);
        when(program.getTargetAgeMin()).thenReturn(3);
        when(program.getTargetAgeMax()).thenReturn(13);
        when(program.getClassType()).thenReturn("INDIVIDUAL");
        when(program.getIsRecruiting()).thenReturn(true);
        when(program.getRatingAvg()).thenReturn(new BigDecimal("5.0"));

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(1L).childId(1L)
                .monthlyBudget(MonthlyBudget.UNDER_10)
                .classType(ClassType.INDIVIDUAL)
                .onlinePreference(OnlinePreference.BOTH)
                .createdAt(LocalDateTime.now())
                .build();

        ScoreBreakdownDto score = scoringService.calculate(program, preference, 7, 37.5, 127.0);

        assertThat(score.getTotalScore()).isLessThanOrEqualTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("모집 중인 프로그램은 recruiting 점수를 받는다")
    void recruitingScore_whenRecruiting() {
        Program program = mock(Program.class);
        when(program.getLatitude()).thenReturn(null);
        when(program.getLongitude()).thenReturn(null);
        when(program.getPrice()).thenReturn(50000);
        when(program.getIsFree()).thenReturn(false);
        when(program.getTargetAgeMin()).thenReturn(null);
        when(program.getTargetAgeMax()).thenReturn(null);
        when(program.getClassType()).thenReturn(null);
        when(program.getIsRecruiting()).thenReturn(true);
        when(program.getRatingAvg()).thenReturn(new BigDecimal("3.0"));

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(1L).childId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        ScoreBreakdownDto score = scoringService.calculate(program, preference, 7, 0.0, 0.0);

        assertThat(score.getScoreRecruiting()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("나이 범위 벗어나면 age 점수 0점")
    void ageScore_zeroWhenOutOfRange() {
        Program program = mock(Program.class);
        when(program.getLatitude()).thenReturn(null);
        when(program.getLongitude()).thenReturn(null);
        when(program.getPrice()).thenReturn(0);
        when(program.getIsFree()).thenReturn(true);
        when(program.getTargetAgeMin()).thenReturn(10);
        when(program.getTargetAgeMax()).thenReturn(13);
        when(program.getClassType()).thenReturn(null);
        when(program.getIsRecruiting()).thenReturn(false);
        when(program.getRatingAvg()).thenReturn(new BigDecimal("0.0"));

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(1L).childId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        ScoreBreakdownDto score = scoringService.calculate(program, preference, 5, 0.0, 0.0);

        assertThat(score.getScoreAge()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    @DisplayName("Haversine 거리 계산 - 같은 좌표면 0km")
    void haversine_samePoint_returnsZero() {
        double dist = scoringService.haversine(37.5, 127.0, 37.5, 127.0);
        assertThat(dist).isEqualTo(0.0);
    }

    @Test
    @DisplayName("UNDER_10 예산 설정 시 무료 프로그램 만점")
    void budgetScore_freeProgram_fullScore() {
        Program program = mock(Program.class);
        when(program.getLatitude()).thenReturn(null);
        when(program.getLongitude()).thenReturn(null);
        when(program.getPrice()).thenReturn(0);
        when(program.getIsFree()).thenReturn(true);
        when(program.getTargetAgeMin()).thenReturn(null);
        when(program.getTargetAgeMax()).thenReturn(null);
        when(program.getClassType()).thenReturn(null);
        when(program.getIsRecruiting()).thenReturn(false);
        when(program.getRatingAvg()).thenReturn(new BigDecimal("0.0"));

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(1L).childId(1L)
                .monthlyBudget(MonthlyBudget.UNDER_10)
                .createdAt(LocalDateTime.now())
                .build();

        ScoreBreakdownDto score = scoringService.calculate(program, preference, 7, 0.0, 0.0);

        assertThat(score.getScoreBudget()).isEqualTo(new BigDecimal("20.00"));
    }
}