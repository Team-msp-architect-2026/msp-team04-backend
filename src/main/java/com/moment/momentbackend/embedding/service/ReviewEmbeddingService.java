package com.moment.momentbackend.embedding.service;

import com.moment.momentbackend.embedding.client.EmbeddingServiceClient;
import com.moment.momentbackend.embedding.dto.EmbeddingRequestDto;
import com.moment.momentbackend.embedding.dto.EmbeddingResponseDto;
import com.moment.momentbackend.opensearch.service.OpenSearchIndexService;
import com.moment.momentbackend.review.entity.Review;
import com.moment.momentbackend.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewEmbeddingService {

    private final ReviewRepository reviewRepository;
    private final EmbeddingServiceClient embeddingServiceClient;
    private final OpenSearchIndexService openSearchIndexService;

    public void embedAll() {
        List<Review> reviews = reviewRepository.findAll();
        log.info("[후기 임베딩 시작] 총 {}건", reviews.size());

        int successCount = 0;
        int failCount = 0;

        for (Review review : reviews) {
            String normalizedText = normalize(review);
            EmbeddingRequestDto request = new EmbeddingRequestDto(
                    review.getId(), "REVIEW", normalizedText
            );

            EmbeddingResponseDto response = embeddingServiceClient.embed(request);

            if (response.isSuccess()) {
                // TODO: pgvector 설치 후 review.setEmbedding(response.getVector()) 추가
                openSearchIndexService.upsertReview(
                        review.getId(),
                        review.getProgramId(),
                        review.getRating() != null ? review.getRating().floatValue() : 0f,
                        response.getVector()
                );
                successCount++;
            } else {
                failCount++;
            }
        }

        log.info("[후기 임베딩 완료] 성공={}, 실패={}", successCount, failCount);
    }

    private String normalize(Review review) {
        return String.format("평점: %s | 내용: %s",
                review.getRating() != null ? review.getRating().toPlainString() : "0",
                nullSafe(review.getContent())
        );
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}