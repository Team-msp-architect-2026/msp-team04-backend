package com.moment.momentbackend.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)
public class SemanticSearchResponseDto {

    private Long programId;
    private String title;
    private String category;
    private String region;
    private Integer price;
    private Boolean isFree;
    private String imageUrl;
    private BigDecimal ratingAvg;
    private Integer reviewCount;
    private Boolean isRecruiting;
    private Double semanticScore;
    private Double rerankScore;
}
