package com.moment.momentbackend.report.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParentingReportGenerateResponse(
        Long childId,
        Integer supportCount,
        Integer freeProgramCount,
        Integer recommendCount,
        Integer totalMonthlySaving,
        BigDecimal aiMatchScore,
        String summaryMessage,
        String savingMessage,
        String benefitMessage,
        String recommendationMessage,
        String source,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
