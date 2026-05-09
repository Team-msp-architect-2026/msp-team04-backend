package com.moment.momentbackend.batch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgramCsvDto {
    private String externalSource;
    private String externalId;
    private String title;
    private String category;
    private String description;
    private String programType;
    private Integer targetAgeMin;
    private Integer targetAgeMax;
    private Integer price;
    private Boolean isFree;
    private String region;
    private String detailAddress;
    private Double latitude;
    private Double longitude;
    private String classType;
    private Boolean isRecruiting;
    private Integer maxCapacity;
    private Integer remainCapacity;
}