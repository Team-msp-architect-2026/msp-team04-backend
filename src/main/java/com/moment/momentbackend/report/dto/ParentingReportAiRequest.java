package com.moment.momentbackend.report.dto;

public record ParentingReportAiRequest(
        ParentingReportChildRequest childInfo,
        Integer supportCount,
        Integer freeProgramCount,
        Integer recommendCount,
        ParentingReportSavingsRequest savingsBreakdown,
        String calculationBasis
) {
}
