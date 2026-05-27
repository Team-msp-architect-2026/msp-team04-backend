package com.moment.momentbackend.report.dto;

import java.util.List;

public record ParentingReportChildRequest(
        String childName,
        Integer age,
        List<String> concerns,
        String region,
        String monthlyBudget
) {
}
