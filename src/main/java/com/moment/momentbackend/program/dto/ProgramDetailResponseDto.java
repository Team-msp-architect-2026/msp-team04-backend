package com.moment.momentbackend.program.dto;

import com.moment.momentbackend.program.entity.Program;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProgramDetailResponseDto {

    // 기본정보
    private Long id;
    private String name;
    private String institutionName;
    private String category;
    private String programType;
    private Integer price;
    private Boolean isFree;
    private Integer targetAgeMin;
    private Integer targetAgeMax;
    private String description;
    private String imageUrl;
    private String contactPhone;
    private String contactUrl;

    // 운영정보
    private String operationStart;
    private String operationEnd;
    private String classTime;
    private String classType;

    // 모집현황
    private Integer maxCapacity;
    private Integer remainCapacity;
    private Boolean isRecruiting;
    private String deadlineDate;

    // 위치정보
    private String region;
    private String detailAddress;
    private Double latitude;
    private Double longitude;

    // 커리큘럼
    private String curriculum;

    // 태그
    private List<String> tags;

    // 평점/후기
    private Double ratingAvg;
    private Integer reviewCount;

    public ProgramDetailResponseDto(Program program) {
        this.id = program.getId();
        this.name = program.getTitle();
        this.institutionName = program.getInstitution() != null
                ? program.getInstitution().getInstitutionName() : null;
        this.category = program.getCategory();
        this.programType = program.getProgramType();
        this.price = program.getPrice();
        this.isFree = program.getIsFree();
        this.targetAgeMin = program.getTargetAgeMin();
        this.targetAgeMax = program.getTargetAgeMax();
        this.description = program.getDescription();
        this.imageUrl = program.getImageUrl();
        this.contactPhone = program.getContactPhone();
        this.contactUrl = program.getContactUrl();

        this.operationStart = program.getOperationStart() != null
                ? program.getOperationStart().toString() : null;
        this.operationEnd = program.getOperationEnd() != null
                ? program.getOperationEnd().toString() : null;
        this.classTime = program.getClassTime();
        this.classType = program.getClassType();

        this.maxCapacity = program.getMaxCapacity();
        this.remainCapacity = program.getRemainCapacity();
        this.isRecruiting = program.getIsRecruiting();
        this.deadlineDate = program.getDeadlineDate() != null
                ? program.getDeadlineDate().toString() : null;

        this.region = program.getRegion();
        this.detailAddress = program.getDetailAddress();
        this.latitude = program.getLatitude() != null
                ? program.getLatitude().doubleValue() : null;
        this.longitude = program.getLongitude() != null
                ? program.getLongitude().doubleValue() : null;

        this.curriculum = program.getCurriculum();

        this.tags = program.getTags().stream()
                .map(t -> t.getTag())
                .collect(Collectors.toList());

        this.ratingAvg = program.getRatingAvg() != null
                ? program.getRatingAvg().doubleValue() : 0.0;
        this.reviewCount = program.getReviewCount();
    }
}