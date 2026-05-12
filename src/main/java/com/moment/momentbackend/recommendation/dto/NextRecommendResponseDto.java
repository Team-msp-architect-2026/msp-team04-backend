package com.moment.momentbackend.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class NextRecommendResponseDto {

    private Long appliedProgramId;
    private String appliedProgramTitle;
    private String appliedProgramCategory;
    private List<NextProgramDto> nextRecommendations;

    @Getter
    @Builder
    public static class NextProgramDto {
        private Long programId;
        private String title;
        private String category;
        private String classTime;
        private BigDecimal ratingAvg;
        private String imageUrl;
        private String reason;
    }
}