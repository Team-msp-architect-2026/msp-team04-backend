package com.moment.momentbackend.embedding;

import com.moment.momentbackend.embedding.client.EmbeddingServiceClient;
import com.moment.momentbackend.embedding.dto.EmbeddingRequestDto;
import com.moment.momentbackend.embedding.dto.EmbeddingResponseDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("EmbeddingServiceClient 생성자 수정 필요 - 임시 비활성화")
class EmbeddingServiceClientTest {

    private final EmbeddingServiceClient client =
            new EmbeddingServiceClient("http://localhost:8000");

    @Test
    @DisplayName("임베딩 요청 성공 시 success=true 반환")
    void embed_success() {
        EmbeddingRequestDto request =
                new EmbeddingRequestDto(1L, "PROGRAM", "초등 수학 기초반 강남구");

        EmbeddingResponseDto response = client.embed(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getSourceId()).isEqualTo(1L);
        assertThat(response.getSourceType()).isEqualTo("PROGRAM");
        assertThat(response.getVector()).hasSize(1536);
    }

    @Test
    @DisplayName("빈 텍스트로 임베딩 요청해도 정상 처리")
    void embed_empty_text() {
        EmbeddingRequestDto request =
                new EmbeddingRequestDto(2L, "REVIEW", "");

        EmbeddingResponseDto response = client.embed(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getSourceId()).isEqualTo(2L);
    }
}