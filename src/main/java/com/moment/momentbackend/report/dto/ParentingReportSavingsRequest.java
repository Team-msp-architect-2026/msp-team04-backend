package com.moment.momentbackend.report.dto;

public record ParentingReportSavingsRequest(
        Integer childcareSupportAmount,
        Integer educationVoucherAmount,
        Integer freeProgramAmount,
        Integer totalMonthlySaving
) {
}
