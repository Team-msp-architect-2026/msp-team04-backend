package com.moment.momentbackend.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class StartDateResponseDto {

    private Long programId;
    private String programTitle;
    private LocalDate operationStart;
    private LocalDate operationEnd;
    private String classTime;
    private String classType;
    private Integer targetAgeMin;
    private Integer targetAgeMax;
    private Integer childAge;
    private Boolean isAgeEligible;
    private LocalDate optimalStartDate;
    private String message;
}