package com.moment.momentbackend.embedding.client;

import com.moment.momentbackend.embedding.dto.EmbeddingRequestDto;
import com.moment.momentbackend.embedding.dto.EmbeddingResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmbeddingServiceClient {

    private static final int MAX_RETRY = 3;

    // TODO: OpenAI API 키 설정 후 실제 호출로 교체
    // @Value("${openai.api-key}")
    // private String apiKey;

    public EmbeddingResponseDto embed(EmbeddingRequestDto request) {
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                log.info("[임베딩 시도 {}/{}] sourceType={}, sourceId={}",
                        attempt, MAX_RETRY, request.getSourceType(), request.getSourceId());

                // TODO: 실제 OpenAI Embedding API 호출로 교체
                // POST https://api.openai.com/v1/embeddings
                // model: text-embedding-3-small
                float[] dummyVector = new float[1536];
                return new EmbeddingResponseDto(
                        request.getSourceId(),
                        request.getSourceType(),
                        dummyVector,
                        true
                );

            } catch (Exception e) {
                log.error("[임베딩 실패 {}/{}] sourceType={}, sourceId={}, error={}",
                        attempt, MAX_RETRY,
                        request.getSourceType(), request.getSourceId(),
                        e.getMessage());

                if (attempt == MAX_RETRY) {
                    log.error("[임베딩 최종 실패] sourceType={}, sourceId={}",
                            request.getSourceType(), request.getSourceId());
                    return new EmbeddingResponseDto(
                            request.getSourceId(),
                            request.getSourceType(),
                            null,
                            false
                    );
                }
            }
        }
        return new EmbeddingResponseDto(request.getSourceId(), request.getSourceType(), null, false);
    }
}