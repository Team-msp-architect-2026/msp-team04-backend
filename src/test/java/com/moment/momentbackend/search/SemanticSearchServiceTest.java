package com.moment.momentbackend.search;

import com.moment.momentbackend.embedding.client.EmbeddingServiceClient;
import com.moment.momentbackend.embedding.dto.EmbeddingRequestDto;
import com.moment.momentbackend.embedding.dto.EmbeddingResponseDto;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.search.dto.SemanticSearchResponseDto;
import com.moment.momentbackend.search.service.SemanticSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensearch.client.opensearch.OpenSearchClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SemanticSearchServiceTest {

    @InjectMocks
    private SemanticSearchService semanticSearchService;

    @Mock
    private EmbeddingServiceClient embeddingServiceClient;

    @Mock
    private OpenSearchClient openSearchClient;

    @Mock
    private ProgramRepository programRepository;

    @Test
    @DisplayName("임베딩 실패 시 빈 배열 반환")
    void search_embeddingFailed_returnsEmptyList() {
        given(embeddingServiceClient.embed(any(EmbeddingRequestDto.class)))
                .willReturn(new EmbeddingResponseDto(0L, "QUERY", null, false));

        List<SemanticSearchResponseDto> result = semanticSearchService.search("미술 수업");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("임베딩 성공하고 vector가 null이면 빈 배열 반환")
    void search_nullVector_returnsEmptyList() {
        given(embeddingServiceClient.embed(any(EmbeddingRequestDto.class)))
                .willReturn(new EmbeddingResponseDto(0L, "QUERY", null, true));

        List<SemanticSearchResponseDto> result = semanticSearchService.search("음악 교실");

        assertThat(result).isEmpty();
    }
}