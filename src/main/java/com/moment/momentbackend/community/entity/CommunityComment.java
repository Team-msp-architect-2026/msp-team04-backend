package com.moment.momentbackend.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "community_comment")
public class CommunityComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer likeCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Builder
    private CommunityComment(Long postId, Long userId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public static CommunityComment create(Long postId, Long userId, String content) {
        return CommunityComment.builder()
                .postId(postId)
                .userId(userId)
                .content(content)
                .build();
    }

    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount -= 1;
        }
    }
}
