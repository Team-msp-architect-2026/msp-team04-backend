package com.moment.momentbackend.recommendation.dto;

import com.moment.momentbackend.recommendation.entity.AiRecommendation;
import com.moment.momentbackend.program.entity.Program;
import lombok.Getter;

@Getter
public class RecommendationResponseDto {

    private Long recommendationId;
    private Long programId;
    private String title;
    private String category;
    private String region;
    private Integer price;
    private Boolean isFree;
    private String classType;
    private Boolean isRecruiting;
    private String imageUrl;
    private Integer rankNo;
    private Boolean isTop3;
    private String recommendReason;
    private ScoreBreakdownDto scoreBreakdown;

    public RecommendationResponseDto(AiRecommendation rec, Program program) {
        this.recommendationId = rec.getId();
        this.programId = program.getId();
        this.title = program.getTitle();
        this.category = program.getCategory();
        this.region = program.getRegion();
        this.price = program.getPrice();
        this.isFree = program.getIsFree();
        this.classType = program.getClassType();
        this.isRecruiting = program.getIsRecruiting();
        this.imageUrl = normalizeImageUrl(program.getImageUrl());
        this.rankNo = rec.getRankNo();
        this.isTop3 = rec.getIsTop3();
        this.recommendReason = rec.getRecommendReason();
        this.scoreBreakdown = ScoreBreakdownDto.builder()
                .scoreDistance(rec.getScoreDistance())
                .scoreBudget(rec.getScoreBudget())
                .scoreAge(rec.getScoreAge())
                .scoreKeyword(rec.getScoreKeyword())
                .scoreClassType(rec.getScoreClassType())
                .scoreRecruiting(rec.getScoreRecruiting())
                .scoreReview(rec.getScoreReview())
                .totalScore(rec.getTotalScore())
                .build();
    }

    private String normalizeImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        if (imageUrl.startsWith("/")) {
            return "https://yeyak.seoul.go.kr" + imageUrl;
        }

        return imageUrl;
    }
}