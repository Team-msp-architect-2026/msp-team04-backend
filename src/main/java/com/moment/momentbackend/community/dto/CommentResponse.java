package com.moment.momentbackend.community.dto;

import com.moment.momentbackend.community.entity.CommunityComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;
    private Integer likeCount;
    private boolean likedByMe;
    private boolean isMine;
    private LocalDateTime createdAt;

    public static CommentResponse of(CommunityComment comment, boolean likedByMe, boolean isMine) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .likedByMe(likedByMe)
                .isMine(isMine)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
