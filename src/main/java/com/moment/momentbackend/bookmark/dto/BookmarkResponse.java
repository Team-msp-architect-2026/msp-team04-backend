package com.moment.momentbackend.bookmark.dto;

import com.moment.momentbackend.program.entity.Program;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class BookmarkResponse {

    private Long programId;
    private String title;
    private String category;
    private String region;
    private String imageUrl;
    private Integer price;
    private Boolean isFree;
    private BigDecimal ratingAvg;
    private Integer reviewCount;
    private Boolean isRecruiting;

    public static BookmarkResponse from(Program program) {
        return BookmarkResponse.builder()
                .programId(program.getId())
                .title(program.getTitle())
                .category(program.getCategory())
                .region(program.getRegion())
                .imageUrl(program.getImageUrl())
                .price(program.getPrice())
                .isFree(program.getIsFree())
                .ratingAvg(program.getRatingAvg())
                .reviewCount(program.getReviewCount())
                .isRecruiting(program.getIsRecruiting())
                .build();
    }
}
