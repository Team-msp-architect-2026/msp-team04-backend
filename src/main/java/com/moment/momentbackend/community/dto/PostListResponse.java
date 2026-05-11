package com.moment.momentbackend.community.dto;

import com.moment.momentbackend.community.entity.CommunityPost;
import com.moment.momentbackend.community.type.PostCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListResponse {

    private Long postId;
    private Long userId;
    private PostCategory category;
    private String childAge;
    private String title;
    private String contentPreview;
    private String imageUrl;
    private Integer commentCount;
    private Integer likeCount;
    private LocalDateTime createdAt;

    public static PostListResponse of(CommunityPost post) {
        String preview = post.getContent().length() > 100
                ? post.getContent().substring(0, 100) + "..."
                : post.getContent();

        return PostListResponse.builder()
                .postId(post.getId())
                .userId(post.getUserId())
                .category(post.getCategory())
                .childAge(post.getChildAge())
                .title(post.getTitle())
                .contentPreview(preview)
                .imageUrl(post.getImageUrl())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
