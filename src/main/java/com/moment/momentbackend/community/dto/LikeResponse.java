package com.moment.momentbackend.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponse {

    private Long postId;
    private boolean liked;
    private Integer likeCount;

    public static LikeResponse of(Long postId, boolean liked, Integer likeCount) {
        return LikeResponse.builder()
                .postId(postId)
                .liked(liked)
                .likeCount(likeCount)
                .build();
    }
}
