package com.moment.momentbackend.report.dto;

import com.moment.momentbackend.report.entity.AiReport;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class AiReportResponseDto {

    private final Long id;
    private final Long childId;
    private final Integer totalSupportCount;
    private final Integer totalFreeProgramCount;
    private final Integer totalRecommendCount;
    private final Integer totalMonthlySaving;
    private final BigDecimal aiMatchScore;
    private final String summaryMessage;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private AiReportResponseDto(AiReport entity) {
        this.id = entity.getId();
        this.childId = entity.getChildId();
        this.totalSupportCount = entity.getTotalSupportCount();
        this.totalFreeProgramCount = entity.getTotalFreeProgramCount();
        this.totalRecommendCount = entity.getTotalRecommendCount();
        this.totalMonthlySaving = entity.getTotalMonthlySaving();
        this.aiMatchScore = entity.getAiMatchScore();
        this.summaryMessage = entity.getSummaryMessage();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

    public static AiReportResponseDto from(AiReport entity) {
        return new AiReportResponseDto(entity);
    }
}