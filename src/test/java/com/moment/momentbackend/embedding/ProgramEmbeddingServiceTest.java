package com.moment.momentbackend.embedding;

import com.moment.momentbackend.embedding.client.EmbeddingServiceClient;
import com.moment.momentbackend.embedding.dto.EmbeddingRequestDto;
import com.moment.momentbackend.embedding.dto.EmbeddingResponseDto;
import com.moment.momentbackend.embedding.service.ProgramEmbeddingService;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgramEmbeddingServiceTest {

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private EmbeddingServiceClient embeddingServiceClient;

    @InjectMocks
    private ProgramEmbeddingService programEmbeddingService;

    @Test
    @DisplayName("프로그램 목록 임베딩 전체 처리")
    void embedAll_success() {
        // given
        Program program = mock(Program.class);
        when(program.getId()).thenReturn(1L);
        when(program.getTitle()).thenReturn("초등 수학 기초반");
        when(program.getCategory()).thenReturn("EDUCATION");
        when(program.getDescription()).thenReturn("수학 기초 수업");
        when(program.getRegion()).thenReturn("강남구");
        when(program.getTargetAgeMin()).thenReturn(6);
        when(program.getTargetAgeMax()).thenReturn(9);
        when(programRepository.findAll()).thenReturn(List.of(program));
        when(embeddingServiceClient.embed(any(EmbeddingRequestDto.class)))
                .thenReturn(new EmbeddingResponseDto(1L, "PROGRAM", new float[1536], true));

        // when
        programEmbeddingService.embedAll();

        // then
        verify(programRepository, times(1)).findAll();
        verify(embeddingServiceClient, times(1)).embed(any());
    }

    @Test
    @DisplayName("프로그램 필드가 null이어도 정규화 시 오류 없음")
    void embedAll_null_fields() {
        // given
        Program program = mock(Program.class);
        when(program.getId()).thenReturn(1L);
        when(program.getTitle()).thenReturn(null);
        when(program.getCategory()).thenReturn(null);
        when(program.getDescription()).thenReturn(null);
        when(program.getRegion()).thenReturn(null);
        when(program.getTargetAgeMin()).thenReturn(0);
        when(program.getTargetAgeMax()).thenReturn(0);
        when(programRepository.findAll()).thenReturn(List.of(program));
        when(embeddingServiceClient.embed(any(EmbeddingRequestDto.class)))
                .thenReturn(new EmbeddingResponseDto(1L, "PROGRAM", null, false));

        // when & then (예외 없이 실행되면 통과)
        programEmbeddingService.embedAll();
    }
}