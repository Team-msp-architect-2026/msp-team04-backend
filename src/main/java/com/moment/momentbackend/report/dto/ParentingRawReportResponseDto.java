package com.moment.momentbackend.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ParentingRawReportResponseDto {

    private ChildInfoDto childInfo;
    private Integer supportCount;
    private Integer freeProgramCount;
    private Integer recommendCount;
    private SavingsBreakdownDto savingsBreakdown;
    private String calculationBasis;

    @Getter
    @Builder
    public static class ChildInfoDto {
        private String childName;
        private int age;
        private List<String> concerns;
        private String region;
        private String monthlyBudget;
    }

    @Getter
    @Builder
    public static class SavingsBreakdownDto {
        private Integer childcareSupportAmount;  // 아이돌봄 지원금
        private Integer educationVoucherAmount;  // 교육비 바우처
        private Integer freeProgramAmount;       // 무료 프로그램 활용액
        private Integer totalMonthlySaving;      // 총 절감액
    }
}