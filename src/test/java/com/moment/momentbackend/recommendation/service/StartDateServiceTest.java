package com.moment.momentbackend.recommendation.service;

import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.recommendation.dto.StartDateResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StartDateServiceTest {

    @InjectMocks
    private StartDateService startDateService;

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private ChildProfileRepository childProfileRepository;

    // ── 공통 픽스처 ──────────────────────────────────────────────

    private ChildProfile mockChild(LocalDate birthDate) {
        ChildProfile child = mock(ChildProfile.class);
        given(child.getBirthDate()).willReturn(birthDate);
        return child;
    }

    private Program mockProgram(LocalDate operationStart, Integer ageMin, Integer ageMax) {
        Program program = mock(Program.class);
        given(program.getId()).willReturn(1L);
        given(program.getTitle()).willReturn("테스트 프로그램");
        given(program.getOperationStart()).willReturn(operationStart);
        given(program.getOperationEnd()).willReturn(LocalDate.now().plusMonths(3));
        given(program.getClassTime()).willReturn("10:00");
        given(program.getClassType()).willReturn("온라인");
        given(program.getTargetAgeMin()).willReturn(ageMin);
        given(program.getTargetAgeMax()).willReturn(ageMax);
        return program;
    }

    // ── 시작일 계산 ───────────────────────────────────────────────

    @Test
    @DisplayName("operationStart가 오늘 이후이면 optimalStartDate = operationStart")
    void getStartDate_futureOperationStart_returnsOperationStart() {
        LocalDate futureDate = LocalDate.now().plusDays(10);
        ChildProfile child = mockChild(LocalDate.now().minusYears(5));
        Program program = mockProgram(futureDate, 3, 8);

        given(childProfileRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(child));
        given(programRepository.findById(1L)).willReturn(Optional.of(program));

        StartDateResponseDto result = startDateService.getStartDate(1L, 1L, 1L);

        assertThat(result.getOptimalStartDate()).isEqualTo(futureDate);
        assertThat(result.getMessage()).contains(String.valueOf(futureDate.getMonthValue()));
        assertThat(result.getMessage()).contains(String.valueOf(futureDate.getDayOfMonth()));
    }

    @Test
    @DisplayName("operationStart가 과거이면 optimalStartDate = today")
    void getStartDate_pastOperationStart_returnsToday() {
        LocalDate pastDate = LocalDate.now().minusDays(5);
        ChildProfile child = mockChild(LocalDate.now().minusYears(5));
        Program program = mockProgram(pastDate, null, null);

        given(childProfileRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(child));
        given(programRepository.findById(1L)).willReturn(Optional.of(program));

        StartDateResponseDto result = startDateService.getStartDate(1L, 1L, 1L);

        assertThat(result.getOptimalStartDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("operationStart가 null이면 optimalStartDate = today")
    void getStartDate_nullOperationStart_returnsToday() {
        ChildProfile child = mockChild(LocalDate.now().minusYears(5));
        Program program = mockProgram(null, null, null);

        given(childProfileRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(child));
        given(programRepository.findById(1L)).willReturn(Optional.of(program));

        StartDateResponseDto result = startDateService.getStartDate(1L, 1L, 1L);

        assertThat(result.getOptimalStartDate()).isEqualTo(LocalDate.now());
    }

    // ── 나이 적합성 ───────────────────────────────────────────────

    @Test
    @DisplayName("아이 나이가 범위 내이면 isAgeEligible = true")
    void getStartDate_ageInRange_eligible() {
        ChildProfile child = mockChild(LocalDate.now().minusYears(5));
        Program program = mockProgram(null, 3, 8);

        given(childProfileRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(child));
        given(programRepository.findById(1L)).willReturn(Optional.of(program));

        StartDateResponseDto result = startDateService.getStartDate(1L, 1L, 1L);

        assertThat(result.getIsAgeEligible()).isTrue();
        assertThat(result.getChildAge()).isEqualTo(5);
    }

    @Test
    @DisplayName("아이 나이가 targetAgeMin보다 작으면 isAgeEligible = false")
    void getStartDate_ageTooYoung_notEligible() {
        ChildProfile child = mockChild(LocalDate.now().minusYears(2));
        Program program = mockProgram(null, 5, 10);

        given(childProfileRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(child));
        given(programRepository.findById(1L)).willReturn(Optional.of(program));

        StartDateResponseDto result = startDateService.getStartDate(1L, 1L, 1L);

        assertThat(result.getIsAgeEligible()).isFalse();
    }

    @Test
    @DisplayName("아이 나이가 targetAgeMax보다 크면 isAgeEligible = false")
    void getStartDate_ageTooOld_notEligible() {
        ChildProfile child = mockChild(LocalDate.now().minusYears(12));
        Program program = mockProgram(null, 5, 10);

        given(childProfileRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(child));
        given(programRepository.findById(1L)).willReturn(Optional.of(program));

        StartDateResponseDto result = startDateService.getStartDate(1L, 1L, 1L);

        assertThat(result.getIsAgeEligible()).isFalse();
    }

    @Test
    @DisplayName("targetAgeMin/Max가 null이면 나이 제한 없이 isAgeEligible = true")
    void getStartDate_nullAgeRange_eligible() {
        ChildProfile child = mockChild(LocalDate.now().minusYears(20));
        Program program = mockProgram(null, null, null);

        given(childProfileRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(child));
        given(programRepository.findById(1L)).willReturn(Optional.of(program));

        StartDateResponseDto result = startDateService.getStartDate(1L, 1L, 1L);

        assertThat(result.getIsAgeEligible()).isTrue();
    }

    // ── 예외 ─────────────────────────────────────────────────────

    @Test
    @DisplayName("자녀 프로필을 찾지 못하면 CHILD_ACCESS_DENIED 예외")
    void getStartDate_childNotFound_throwsException() {
        given(childProfileRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> startDateService.getStartDate(1L, 1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHILD_ACCESS_DENIED);
    }

    @Test
    @DisplayName("프로그램을 찾지 못하면 PROGRAM_NOT_FOUND 예외")
    void getStartDate_programNotFound_throwsException() {
        ChildProfile child = mockChild(LocalDate.now().minusYears(5));
        given(childProfileRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(child));
        given(programRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> startDateService.getStartDate(1L, 1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROGRAM_NOT_FOUND);
    }
}