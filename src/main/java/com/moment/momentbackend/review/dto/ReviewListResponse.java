package com.moment.momentbackend.review.dto;

import com.moment.momentbackend.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewListResponse {

    private Long reviewId;
    private BigDecimal rating;
    private String content;
    private LocalDateTime createdAt;

    public static ReviewListResponse of(Review review) {
        return ReviewListResponse.builder()
                .reviewId(review.getId())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
