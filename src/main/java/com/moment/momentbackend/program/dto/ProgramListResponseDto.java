package com.moment.momentbackend.program.dto;

import com.moment.momentbackend.program.entity.Program;
import lombok.Getter;

@Getter
public class ProgramListResponseDto {

    private Long id;
    private String name;
    private String category;
    private Integer price;
    private Boolean isFree;
    private Integer maxCapacity;
    private Integer remainCapacity;
    private Boolean isRecruiting;
    private String region;
    private String detailAddress;
    private String imageUrl;
    private String classType;
    private String deadlineDate;
    private Double ratingAvg;
    private Integer reviewCount;
    private String description;

    public ProgramListResponseDto(Program program) {
        this.id = program.getId();
        this.name = program.getTitle();
        this.category = program.getCategory();
        this.price = program.getPrice();
        this.isFree = program.getIsFree();
        this.maxCapacity = program.getMaxCapacity();
        this.remainCapacity = program.getRemainCapacity();
        this.isRecruiting = program.getIsRecruiting();
        this.region = program.getRegion();
        this.detailAddress = program.getDetailAddress();
        this.imageUrl = program.getImageUrl();
        this.classType = program.getClassType();
        this.deadlineDate = program.getDeadlineDate() != null
                ? program.getDeadlineDate().toString() : null;
        this.ratingAvg = program.getRatingAvg() != null
                ? program.getRatingAvg().doubleValue() : 0.0;
        this.reviewCount = program.getReviewCount();
        this.description = program.getDescription();
    }
}