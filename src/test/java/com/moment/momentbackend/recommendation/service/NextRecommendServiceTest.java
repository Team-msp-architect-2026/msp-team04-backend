package com.moment.momentbackend.recommendation.service;

import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.application.repository.ApplicationRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.global.metrics.BusinessMetricsService;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.recommendation.dto.NextRecommendResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NextRecommendServiceTest {

    @InjectMocks
    private NextRecommendService nextRecommendService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private BusinessMetricsService businessMetricsService;

    @BeforeEach
    void setUpBusinessMetrics() {
        given(businessMetricsService.recordRecommendation(anyString(), any()))
                .willAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });
    }

    // ── 공통 픽스처 ──────────────────────────────────────────────

    private Application mockApplication(Long programId) {
        Application application = mock(Application.class);
        given(application.getProgramId()).willReturn(programId);
        return application;
    }

    // applied 프로그램용 (id, title, category만 stub)
    private Program mockAppliedProgram(Long id, String category) {
        Program program = mock(Program.class);
        given(program.getId()).willReturn(id);
        given(program.getTitle()).willReturn("프로그램 " + id);
        given(program.getCategory()).willReturn(category);
        return program;
    }

    // 추천 프로그램용 (모든 필드 stub)
    private Program mockProgram(Long id, String category) {
        Program program = mock(Program.class);
        given(program.getId()).willReturn(id);
        given(program.getTitle()).willReturn("프로그램 " + id);
        given(program.getCategory()).willReturn(category);
        given(program.getClassTime()).willReturn("10:00");
        given(program.getRatingAvg()).willReturn(BigDecimal.valueOf(4.5));
        given(program.getImageUrl()).willReturn("https://image.url/" + id);
        return program;
    }

    // ── 보완 카테고리 조회 ────────────────────────────────────────

    @Test
    @DisplayName("COMPLEMENT_MAP에 있는 카테고리는 findComplementaryPrograms 호출")
    void getNextRecommend_knownCategory_usesComplementaryPrograms() {
        Application application = mockApplication(10L);
        Program applied = mockAppliedProgram(10L, "미술");
        List<Program> complementPrograms = List.of(
                mockProgram(2L, "음악"),
                mockProgram(3L, "체육"),
                mockProgram(4L, "창의")
        );

        given(applicationRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(application));
        given(programRepository.findById(10L)).willReturn(Optional.of(applied));
        given(programRepository.findComplementaryPrograms(List.of("음악", "체육", "창의")))
                .willReturn(complementPrograms);

        NextRecommendResponseDto result = nextRecommendService.getNextRecommend(1L, 1L);

        assertThat(result.getNextRecommendations()).hasSize(3);
        assertThat(result.getAppliedProgramCategory()).isEqualTo("미술");
        then(programRepository).should(never()).findOtherRecruitingPrograms(any());
    }

    @Test
    @DisplayName("COMPLEMENT_MAP에 없는 카테고리는 findOtherRecruitingPrograms 폴백")
    void getNextRecommend_unknownCategory_fallsBackToOtherPrograms() {
        Application application = mockApplication(10L);
        Program applied = mockAppliedProgram(10L, "요리");
        List<Program> otherPrograms = List.of(
                mockProgram(2L, "미술"),
                mockProgram(3L, "음악")
        );

        given(applicationRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(application));
        given(programRepository.findById(10L)).willReturn(Optional.of(applied));
        given(programRepository.findOtherRecruitingPrograms("요리")).willReturn(otherPrograms);

        NextRecommendResponseDto result = nextRecommendService.getNextRecommend(1L, 1L);

        assertThat(result.getNextRecommendations()).hasSize(2);
        then(programRepository).should(never()).findComplementaryPrograms(any());
    }

    @Test
    @DisplayName("결과가 3개 초과이면 상위 3개만 반환")
    void getNextRecommend_moreThanThree_returnsTopThree() {
        Application application = mockApplication(10L);
        Program applied = mockAppliedProgram(10L, "수학");
        List<Program> programs = List.of(
                mockProgram(1L, "과학"),
                mockProgram(2L, "코딩"),
                mockProgram(3L, "영어"),
                mockProgram(4L, "독서")
        );

        given(applicationRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(application));
        given(programRepository.findById(10L)).willReturn(Optional.of(applied));
        given(programRepository.findComplementaryPrograms(List.of("과학", "코딩", "영어")))
                .willReturn(programs);

        NextRecommendResponseDto result = nextRecommendService.getNextRecommend(1L, 1L);

        assertThat(result.getNextRecommendations()).hasSize(3);
    }

    @Test
    @DisplayName("추천 이유 메시지에 신청 프로그램 카테고리가 포함됨")
    void getNextRecommend_reasonContainsAppliedCategory() {
        Application application = mockApplication(10L);
        Program applied = mockAppliedProgram(10L, "음악");
        List<Program> programs = List.of(mockProgram(2L, "미술"));

        given(applicationRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(application));
        given(programRepository.findById(10L)).willReturn(Optional.of(applied));
        given(programRepository.findComplementaryPrograms(List.of("미술", "체육", "댄스")))
                .willReturn(programs);

        NextRecommendResponseDto result = nextRecommendService.getNextRecommend(1L, 1L);

        assertThat(result.getNextRecommendations().get(0).getReason()).contains("음악");
    }

    // ── 예외 ─────────────────────────────────────────────────────

    @Test
    @DisplayName("신청 내역을 찾지 못하면 NOT_FOUND 예외")
    void getNextRecommend_applicationNotFound_throwsException() {
        given(applicationRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> nextRecommendService.getNextRecommend(1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.APPLICATION_NOT_FOUND);
    }

    @Test
    @DisplayName("신청한 프로그램을 찾지 못하면 PROGRAM_NOT_FOUND 예외")
    void getNextRecommend_programNotFound_throwsException() {
        Application application = mockApplication(10L);
        given(applicationRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(application));
        given(programRepository.findById(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> nextRecommendService.getNextRecommend(1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROGRAM_NOT_FOUND);
    }
}