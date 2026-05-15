package com.moment.momentbackend.embedding.controller;

import com.moment.momentbackend.embedding.dto.EmbeddingJobResponseDto;
import com.moment.momentbackend.embedding.service.ProgramEmbeddingService;
import com.moment.momentbackend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/embeddings")
public class EmbeddingController {

    private final ProgramEmbeddingService programEmbeddingService;

    @PostMapping("/programs")
    public ApiResponse<EmbeddingJobResponseDto> embedPrograms() {
        return ApiResponse.ok(programEmbeddingService.embedAll());
    }
}
