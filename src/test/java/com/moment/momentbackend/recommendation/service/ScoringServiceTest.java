package com.moment.momentbackend.recommendation.service;

import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.entity.ProgramTag;
import com.moment.momentbackend.recommendation.dto.ScoreBreakdownDto;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ScoringServiceTest {

    private ScoringService scoringService;
    private Program program;
    private RecommendationPreference preference;

    @BeforeEach
    void setUp() {
        scoringService = new ScoringService();
        program = mock(Program.class);
        preference = mock(RecommendationPreference.class);

        when(program.getIsRecruiting()).thenReturn(true);
        when(program.getRatingAvg()).thenReturn(new BigDecimal("0.00"));
        when(program.getTargetAgeMin()).thenReturn(null);
        when(program.getTargetAgeMax()).thenReturn(null);
        when(program.getClassType()).thenReturn(null);
        when(program.getLatitude()).thenReturn(null);
        when(program.getLongitude()).thenReturn(null);
        when(program.getPrice()).thenReturn(0);
        when(program.getTags()).thenReturn(List.of());
    }

    @Test
    @DisplayName("거리 1km 이하 → 만점(20점)")
    void distance_within1km_fullScore() {
        when(program.getLatitude()).thenReturn(new BigDecimal("37.5670"));
        when(program.getLongitude()).thenReturn(new BigDecimal("126.9784"));

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 7, 37.5670, 126.9784, List.of());

        assertThat(score.getScoreDistance()).isEqualByComparingTo("20.00");
    }

    @Test
    @DisplayName("거리 3km 초과 5km 이하 → 60%")
    void distance_between3and5km_60percent() {
        when(program.getLatitude()).thenReturn(new BigDecimal("37.5300"));
        when(program.getLongitude()).thenReturn(new BigDecimal("126.9784"));

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 5, 37.5670, 126.9784, List.of());

        assertThat(score.getScoreDistance()).isEqualByComparingTo("12.00");
    }

    @Test
    @DisplayName("거리 10km 초과 → 10%")
    void distance_over10km_10percent() {
        when(program.getLatitude()).thenReturn(new BigDecimal("37.4500"));
        when(program.getLongitude()).thenReturn(new BigDecimal("126.9784"));

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 7, 37.5670, 126.9784, List.of());

        assertThat(score.getScoreDistance()).isEqualByComparingTo("2.00");
    }

    @Test
    @DisplayName("좌표 없음 → 50% 폴백")
    void distance_noCoords_halfScore() {
        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 5, 0.0, 0.0, List.of());

        assertThat(score.getScoreDistance()).isEqualByComparingTo("10.00");
    }

    @Test
    @DisplayName("UNDER_10 예산, price=100000 → 만점")
    void budget_under10_exactBoundary_fullScore() {
        when(program.getPrice()).thenReturn(100000);
        when(preference.getMonthlyBudget()).thenReturn( MonthlyBudget.ZERO_TO_TEN);

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 5, 0.0, 0.0, List.of());

        assertThat(score.getScoreBudget()).isEqualByComparingTo("20.00");
    }

    @Test
    @DisplayName("UNDER_10 예산, price=100001 → 30%")
    void budget_under10_overBoundary_30percent() {
        when(program.getPrice()).thenReturn(100001);
        when(preference.getMonthlyBudget()).thenReturn(MonthlyBudget.ZERO_TO_TEN);

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 5, 0.0, 0.0, List.of());

        assertThat(score.getScoreBudget()).isEqualByComparingTo("6.00");
    }

    @Test
    @DisplayName("OVER_50 예산 → 항상 만점")
    void budget_over50_alwaysFullScore() {
        when(program.getPrice()).thenReturn(9999999);
        when(preference.getMonthlyBudget()).thenReturn(MonthlyBudget.OVER_TWENTY);

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 5, 0.0, 0.0, List.of());

        assertThat(score.getScoreBudget()).isEqualByComparingTo("20.00");
    }

    @Test
    @DisplayName("아이 나이 = targetAgeMin → 만점")
    void age_exactMin_fullScore() {
        when(program.getTargetAgeMin()).thenReturn(5);
        when(program.getTargetAgeMax()).thenReturn(10);

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 5, 0.0, 0.0, List.of());

        assertThat(score.getScoreAge()).isEqualByComparingTo("15.00");
    }

    @Test
    @DisplayName("아이 나이 = targetAgeMax → 만점")
    void age_exactMax_fullScore() {
        when(program.getTargetAgeMin()).thenReturn(5);
        when(program.getTargetAgeMax()).thenReturn(10);

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 10, 0.0, 0.0, List.of());

        assertThat(score.getScoreAge()).isEqualByComparingTo("15.00");
    }

    @Test
    @DisplayName("아이 나이 범위 초과 → 0점")
    void age_outOfRange_zeroScore() {
        when(program.getTargetAgeMin()).thenReturn(5);
        when(program.getTargetAgeMax()).thenReturn(10);

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 11, 0.0, 0.0, List.of());

        assertThat(score.getScoreAge()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("키워드 완전 매칭 → 만점(15점)")
    void keyword_fullMatch_fullScore() {
        ProgramTag tag1 = mock(ProgramTag.class);
        ProgramTag tag2 = mock(ProgramTag.class);
        when(tag1.getTag()).thenReturn("수영");
        when(tag2.getTag()).thenReturn("미술");
        when(program.getTags()).thenReturn(List.of(tag1, tag2));

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 5, 0.0, 0.0, List.of("수영", "미술"));

        assertThat(score.getScoreKeyword()).isEqualByComparingTo("15.00");
    }

    @Test
    @DisplayName("키워드 50% 매칭 → 7.5점")
    void keyword_halfMatch_halfScore() {
        ProgramTag tag1 = mock(ProgramTag.class);
        when(tag1.getTag()).thenReturn("수영");
        when(program.getTags()).thenReturn(List.of(tag1));

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 5, 0.0, 0.0, List.of("수영", "미술"));

        assertThat(score.getScoreKeyword()).isEqualByComparingTo("7.50");
    }

    @Test
    @DisplayName("키워드 매칭 없음 → 0점")
    void keyword_noMatch_zeroScore() {
        ProgramTag tag1 = mock(ProgramTag.class);
        when(tag1.getTag()).thenReturn("음악");
        when(program.getTags()).thenReturn(List.of(tag1));

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 5, 0.0, 0.0, List.of("수영", "미술"));

        assertThat(score.getScoreKeyword()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("concerns 비어있으면 키워드 0점")
    void keyword_emptyConcerns_zeroScore() {
        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 5, 0.0, 0.0, List.of());

        assertThat(score.getScoreKeyword()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("모든 조건 만족 시 총점은 100점 초과 불가")
    void totalScore_neverExceeds100() {
        ProgramTag tag = mock(ProgramTag.class);
        when(tag.getTag()).thenReturn("수영");
        when(program.getTags()).thenReturn(List.of(tag));
        when(program.getLatitude()).thenReturn(new BigDecimal("37.5670"));
        when(program.getLongitude()).thenReturn(new BigDecimal("126.9784"));
        when(program.getPrice()).thenReturn(0);
        when(program.getTargetAgeMin()).thenReturn(1);
        when(program.getTargetAgeMax()).thenReturn(13);
        when(program.getClassType()).thenReturn("ONE_ON_ONE");
        when(program.getIsRecruiting()).thenReturn(true);
        when(program.getRatingAvg()).thenReturn(new BigDecimal("5.00"));
        when(preference.getMonthlyBudget()).thenReturn(MonthlyBudget.OVER_TWENTY);
        when(preference.getClassType()).thenReturn(ClassType.ONE_ON_ONE);

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 7, 37.5670, 126.9784, List.of("수영"));

        assertThat(score.getTotalScore()).isLessThanOrEqualTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("조건 만족 시 ReasonCode 포함 확인")
    void reasonCode_included_whenConditionMet() {
        when(program.getTargetAgeMin()).thenReturn(1);
        when(program.getTargetAgeMax()).thenReturn(13);
        when(program.getPrice()).thenReturn(0);
        when(preference.getMonthlyBudget()).thenReturn(MonthlyBudget.OVER_TWENTY);
        when(program.getIsRecruiting()).thenReturn(true);

        ScoreBreakdownDto score = scoringService.calculate(
                program, preference, 7, 0.0, 0.0, List.of());

        assertThat(score.getReasonCodes()).contains(
                ReasonCode.AGE_FIT,
                ReasonCode.BUDGET_FIT,
                ReasonCode.RECRUITING_OPEN
        );
    }
}