package com.moment.momentbackend.program;

import com.moment.momentbackend.program.dto.ProgramListResponseDto;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramListQueryRepository;
import com.moment.momentbackend.program.service.ProgramService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgramServiceTest {

    @InjectMocks
    private ProgramService programService;

    @Mock
    private ProgramListQueryRepository programListQueryRepository;

    private Program mockProgram(Long id, String category, String region, boolean isRecruiting) {
        Program program = mock(Program.class);
        when(program.getId()).thenReturn(id);
        when(program.getTitle()).thenReturn("테스트 프로그램 " + id);
        when(program.getCategory()).thenReturn(category);
        when(program.getPrice()).thenReturn(50000);
        when(program.getIsFree()).thenReturn(false);
        when(program.getIsRecruiting()).thenReturn(isRecruiting);
        when(program.getRegion()).thenReturn(region);
        when(program.getRatingAvg()).thenReturn(new BigDecimal("4.5"));
        when(program.getReviewCount()).thenReturn(10);
        when(program.getDeadlineDate()).thenReturn(null);
        when(program.getMaxCapacity()).thenReturn(20);
        when(program.getRemainCapacity()).thenReturn(5);
        when(program.getDetailAddress()).thenReturn("서울시 강남구");
        when(program.getImageUrl()).thenReturn(null);
        when(program.getClassType()).thenReturn("SMALL");
        return program;
    }

    @Test
    @DisplayName("필터 없이 전체 프로그램 목록 조회")
    void getPrograms_noFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Program> programs = List.of(
                mockProgram(1L, "EDUCATION", "서울", true),
                mockProgram(2L, "SPORTS", "부산", true)
        );

        when(programListQueryRepository.findPrograms(null, null, null, pageable))
                .thenReturn(new PageImpl<>(programs, pageable, 2));

        Page<ProgramListResponseDto> result = programService.getPrograms(null, null, null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("카테고리 필터 적용 확인")
    void getPrograms_categoryFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Program> programs = List.of(mockProgram(1L, "EDUCATION", "서울", true));

        when(programListQueryRepository.findPrograms(null, "EDUCATION", null, pageable))
                .thenReturn(new PageImpl<>(programs, pageable, 1));

        Page<ProgramListResponseDto> result = programService.getPrograms(null, "EDUCATION", null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo("EDUCATION");
    }

    @Test
    @DisplayName("지역 필터 적용 확인")
    void getPrograms_regionFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Program> programs = List.of(mockProgram(1L, "SPORTS", "서울", true));

        when(programListQueryRepository.findPrograms(null, null, "서울", pageable))
                .thenReturn(new PageImpl<>(programs, pageable, 1));

        Page<ProgramListResponseDto> result = programService.getPrograms(null, null, "서울", pageable);

        assertThat(result.getContent().get(0).getRegion()).isEqualTo("서울");
    }

    @Test
    @DisplayName("RECRUITING 상태 필터 적용 확인")
    void getPrograms_statusFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Program> programs = List.of(
                mockProgram(1L, "EDUCATION", "서울", true),
                mockProgram(2L, "ART", "서울", true)
        );

        when(programListQueryRepository.findPrograms("RECRUITING", null, null, pageable))
                .thenReturn(new PageImpl<>(programs, pageable, 2));

        Page<ProgramListResponseDto> result = programService.getPrograms("RECRUITING", null, null, pageable);

        assertThat(result.getContent()).allMatch(ProgramListResponseDto::getIsRecruiting);
    }

    @Test
    @DisplayName("결과 없을 때 빈 페이지 반환")
    void getPrograms_emptyResult() {
        Pageable pageable = PageRequest.of(0, 10);

        when(programListQueryRepository.findPrograms(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        Page<ProgramListResponseDto> result = programService.getPrograms("RECRUITING", "ETC", "제주", pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }
}