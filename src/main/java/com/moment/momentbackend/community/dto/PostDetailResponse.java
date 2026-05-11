package com.moment.momentbackend.community.dto;

import com.moment.momentbackend.community.entity.CommunityPost;
import com.moment.momentbackend.community.type.PostCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostDetailResponse {

    private Long postId;
    private Long userId;
    private PostCategory category;
    private String childAge;
    private String title;
    private String content;
    private String imageUrl;
    private Integer commentCount;
    private Integer likeCount;
    private boolean isLikedByMe;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostDetailResponse of(CommunityPost post, boolean isLikedByMe) {
        return PostDetailResponse.builder()
                .postId(post.getId())
                .userId(post.getUserId())
                .category(post.getCategory())
                .childAge(post.getChildAge())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .isLikedByMe(isLikedByMe)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
