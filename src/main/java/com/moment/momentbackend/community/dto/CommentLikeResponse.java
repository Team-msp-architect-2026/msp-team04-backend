package com.moment.momentbackend.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentLikeResponse {

    private Long commentId;
    private boolean likedByMe;
    private Integer likeCount;

    public static CommentLikeResponse of(Long commentId, boolean likedByMe, Integer likeCount) {
        return CommentLikeResponse.builder()
                .commentId(commentId)
                .likedByMe(likedByMe)
                .likeCount(likeCount)
                .build();
    }
}
