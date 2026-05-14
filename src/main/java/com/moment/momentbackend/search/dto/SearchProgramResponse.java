package com.moment.momentbackend.search.dto;

import com.moment.momentbackend.program.entity.Program;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchProgramResponse {

    private final Long id;
    private final String name;
    private final String institutionName;
    private final String category;
    private final String description;
    private final Integer price;
    private final Boolean isFree;
    private final Integer maxCapacity;
    private final Integer remainCapacity;
    private final Boolean isRecruiting;
    private final String region;
    private final String detailAddress;
    private final String imageUrl;
    private final String classType;
    private final Integer targetAgeMin;
    private final Integer targetAgeMax;
    private final String deadlineDate;
    private final Double ratingAvg;
    private final Integer reviewCount;
    private final List<String> tags;

    public SearchProgramResponse(Program program) {
        this.id = program.getId();
        this.name = program.getTitle();
        this.institutionName = program.getInstitution() != null
                ? program.getInstitution().getInstitutionName()
                : null;
        this.category = program.getCategory();
        this.description = program.getDescription();
        this.price = program.getPrice();
        this.isFree = program.getIsFree();
        this.maxCapacity = program.getMaxCapacity();
        this.remainCapacity = program.getRemainCapacity();
        this.isRecruiting = program.getIsRecruiting();
        this.region = program.getRegion();
        this.detailAddress = program.getDetailAddress();
        this.imageUrl = program.getImageUrl();
        this.classType = program.getClassType();
        this.targetAgeMin = program.getTargetAgeMin();
        this.targetAgeMax = program.getTargetAgeMax();
        this.deadlineDate = program.getDeadlineDate() != null
                ? program.getDeadlineDate().toString()
                : null;
        this.ratingAvg = program.getRatingAvg() != null
                ? program.getRatingAvg().doubleValue()
                : 0.0;
        this.reviewCount = program.getReviewCount();
        this.tags = program.getTags().stream()
                .map(tag -> tag.getTag())
                .toList();
    }
}
