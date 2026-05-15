package com.moment.momentbackend.embedding.service;

import com.moment.momentbackend.embedding.client.EmbeddingServiceClient;
import com.moment.momentbackend.embedding.dto.EmbeddingJobResponseDto;
import com.moment.momentbackend.embedding.dto.EmbeddingRequestDto;
import com.moment.momentbackend.embedding.dto.EmbeddingResponseDto;
import com.moment.momentbackend.opensearch.service.OpenSearchIndexService;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramEmbeddingService {

    private final ProgramRepository programRepository;
    private final EmbeddingServiceClient embeddingServiceClient;
    private final OpenSearchIndexService openSearchIndexService;

    public EmbeddingJobResponseDto embedAll() {
        List<Program> programs = programRepository.findAll();
        log.info("[프로그램 임베딩 시작] 총 {}건", programs.size());

        int successCount = 0;
        int failCount = 0;

        for (Program program : programs) {
            String normalizedText = normalize(program);
            EmbeddingRequestDto request = new EmbeddingRequestDto(
                    program.getId(),
                    "PROGRAM",
                    normalizedText
            );

            EmbeddingResponseDto response = embeddingServiceClient.embed(request);

            if (response.isSuccess() && response.getVector() != null) {
                openSearchIndexService.upsertProgram(
                        program.getId(),
                        program.getTitle(),
                        program.getCategory(),
                        response.getVector()
                );
                successCount++;
            } else {
                log.warn("[프로그램 임베딩 실패] programId={}, title={}", program.getId(), program.getTitle());
                failCount++;
            }
        }

        log.info("[프로그램 임베딩 완료] 성공={}, 실패={}", successCount, failCount);

        return new EmbeddingJobResponseDto(
                "PROGRAM",
                programs.size(),
                successCount,
                failCount
        );
    }

    private String normalize(Program program) {
        return String.format(
                "제목: %s | 카테고리: %s | 설명: %s | 지역: %s | 대상연령: %s~%s세 | 수업방식: %s | 비용: %s원 | 모집중: %s",
                nullSafe(program.getTitle()),
                nullSafe(program.getCategory()),
                nullSafe(program.getDescription()),
                nullSafe(program.getRegion()),
                program.getTargetAgeMin() != null ? program.getTargetAgeMin() : "",
                program.getTargetAgeMax() != null ? program.getTargetAgeMax() : "",
                nullSafe(program.getClassType()),
                program.getPrice() != null ? program.getPrice() : 0,
                Boolean.TRUE.equals(program.getIsRecruiting()) ? "Y" : "N"
        );
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
