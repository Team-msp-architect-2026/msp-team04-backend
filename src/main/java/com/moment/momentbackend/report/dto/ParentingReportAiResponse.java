package com.moment.momentbackend.report.dto;

public record ParentingReportAiResponse(
        String summaryMessage,
        String savingMessage,
        String benefitMessage,
        String recommendationMessage,
        String source
) {
}
