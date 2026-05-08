package com.moment.momentbackend.program;

import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.program.dto.ProgramDetailResponseDto;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramListQueryRepository;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.program.service.ProgramService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgramDetailServiceTest {

    @InjectMocks
    private ProgramService programService;

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private ProgramListQueryRepository programListQueryRepository;

    private Program mockProgram(Long id) {
        Program program = mock(Program.class);
        when(program.getId()).thenReturn(id);
        when(program.getTitle()).thenReturn("테스트 프로그램");
        when(program.getCategory()).thenReturn("EDUCATION");
        when(program.getPrice()).thenReturn(50000);
        when(program.getIsFree()).thenReturn(false);
        when(program.getIsRecruiting()).thenReturn(true);
        when(program.getRegion()).thenReturn("서울");
        when(program.getDetailAddress()).thenReturn("서울시 강남구");
        when(program.getRatingAvg()).thenReturn(new BigDecimal("4.5"));
        when(program.getReviewCount()).thenReturn(10);
        when(program.getTargetAgeMin()).thenReturn(5);
        when(program.getTargetAgeMax()).thenReturn(10);
        when(program.getLatitude()).thenReturn(new BigDecimal("37.5"));
        when(program.getLongitude()).thenReturn(new BigDecimal("127.0"));
        when(program.getTags()).thenReturn(new ArrayList<>());
        when(program.getInstitution()).thenReturn(null);
        when(program.getOperationStart()).thenReturn(null);
        when(program.getOperationEnd()).thenReturn(null);
        when(program.getDeadlineDate()).thenReturn(null);
        when(program.getDescription()).thenReturn("프로그램 설명");
        when(program.getCurriculum()).thenReturn("커리큘럼 내용");
        when(program.getClassType()).thenReturn("SMALL");
        when(program.getClassTime()).thenReturn("월수금 10:00-12:00");
        when(program.getProgramType()).thenReturn("PRIVATE");
        when(program.getMaxCapacity()).thenReturn(20);
        when(program.getRemainCapacity()).thenReturn(5);
        when(program.getImageUrl()).thenReturn(null);
        when(program.getContactPhone()).thenReturn("02-1234-5678");
        when(program.getContactUrl()).thenReturn(null);
        return program;
    }

    @Test
    @DisplayName("프로그램 상세 조회 성공")
    void getProgram_success() {
        Long programId = 1L;
        Program program = mockProgram(programId);

        when(programRepository.findDetailById(programId)).thenReturn(Optional.of(program));

        ProgramDetailResponseDto result = programService.getProgram(programId);

        assertThat(result.getId()).isEqualTo(programId);
        assertThat(result.getName()).isEqualTo("테스트 프로그램");
        assertThat(result.getCategory()).isEqualTo("EDUCATION");
        assertThat(result.getTargetAgeMin()).isEqualTo(5);
        assertThat(result.getTargetAgeMax()).isEqualTo(10);
        assertThat(result.getRatingAvg()).isEqualTo(4.5);
        assertThat(result.getTags()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 404 예외 발생")
    void getProgram_notFound() {
        when(programRepository.findDetailById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> programService.getProgram(999L))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("태그 목록 포함 확인")
    void getProgram_withTags() {
        Long programId = 1L;
        Program program = mockProgram(programId);

        com.moment.momentbackend.program.entity.ProgramTag tag1 =
                mock(com.moment.momentbackend.program.entity.ProgramTag.class);
        com.moment.momentbackend.program.entity.ProgramTag tag2 =
                mock(com.moment.momentbackend.program.entity.ProgramTag.class);
        when(tag1.getTag()).thenReturn("창의력");
        when(tag2.getTag()).thenReturn("집중력");
        when(program.getTags()).thenReturn(java.util.List.of(tag1, tag2));

        when(programRepository.findDetailById(programId)).thenReturn(Optional.of(program));

        ProgramDetailResponseDto result = programService.getProgram(programId);

        assertThat(result.getTags()).hasSize(2);
        assertThat(result.getTags()).containsExactly("창의력", "집중력");
    }

    @Test
    @DisplayName("기관 정보 포함 확인")
    void getProgram_withInstitution() {
        Long programId = 1L;
        Program program = mockProgram(programId);

        com.moment.momentbackend.program.entity.Institution institution =
                mock(com.moment.momentbackend.program.entity.Institution.class);
        when(institution.getInstitutionName()).thenReturn("테스트 기관");
        when(program.getInstitution()).thenReturn(institution);

        when(programRepository.findDetailById(programId)).thenReturn(Optional.of(program));

        ProgramDetailResponseDto result = programService.getProgram(programId);

        assertThat(result.getInstitutionName()).isEqualTo("테스트 기관");
    }
}