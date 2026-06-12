package com.moment.momentbackend.program.dto;

import com.moment.momentbackend.program.entity.Program;
import lombok.Getter;

@Getter
public class ProgramListResponseDto {

    private Long id;
    private String name;
    private String institutionName;
    private String category;
    private String programType;
    private Integer price;
    private Boolean isFree;
    private Boolean isPublic;
    private Integer targetAgeMin;
    private Integer targetAgeMax;
    private Integer maxCapacity;
    private Integer remainCapacity;
    private Boolean isRecruiting;
    private String region;
    private String detailAddress;
    private String imageUrl;
    private String classType;
    private String classTime;
    private String operationStart;
    private String operationEnd;
    private String deadlineDate;
    private Double ratingAvg;
    private Integer reviewCount;
    private String description;
    private String curriculum;
    private String contactPhone;
    private String contactUrl;

    public ProgramListResponseDto(Program program) {
        this.id = program.getId();
        this.name = program.getTitle();
        this.institutionName = program.getInstitution() != null
                ? program.getInstitution().getInstitutionName() : null;
        this.category = program.getCategory();
        this.programType = program.getProgramType();
        this.price = program.getPrice();
        this.isFree = program.getIsFree();
        this.isPublic = program.getIsPublic();
        this.targetAgeMin = program.getTargetAgeMin();
        this.targetAgeMax = program.getTargetAgeMax();
        this.maxCapacity = program.getMaxCapacity();
        this.remainCapacity = program.getRemainCapacity();
        this.isRecruiting = program.getIsRecruiting();
        this.region = program.getRegion();
        this.detailAddress = program.getDetailAddress();
        this.imageUrl = program.getImageUrl();
        this.classType = program.getClassType();
        this.classTime = program.getClassTime();
        this.operationStart = program.getOperationStart() != null
                ? program.getOperationStart().toString() : null;
        this.operationEnd = program.getOperationEnd() != null
                ? program.getOperationEnd().toString() : null;
        this.deadlineDate = program.getDeadlineDate() != null
                ? program.getDeadlineDate().toString() : null;
        this.ratingAvg = program.getRatingAvg() != null
                ? program.getRatingAvg().doubleValue() : 0.0;
        this.reviewCount = program.getReviewCount();
        this.description = program.getDescription();
        this.curriculum = program.getCurriculum();
        this.contactPhone = program.getContactPhone();
        this.contactUrl = program.getContactUrl();
    }
}
