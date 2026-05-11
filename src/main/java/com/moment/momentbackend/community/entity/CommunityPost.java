package com.moment.momentbackend.community.entity;

import com.moment.momentbackend.community.type.PostCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "community_post")
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory category;

    @Column
    private String childAge;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private Integer commentCount;

    @Column(nullable = false)
    private Integer likeCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Builder
    private CommunityPost(
            Long userId,
            PostCategory category,
            String childAge,
            String title,
            String content,
            String imageUrl
    ) {
        this.userId = userId;
        this.category = category;
        this.childAge = childAge;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.commentCount = 0;
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public static CommunityPost create(
            Long userId,
            PostCategory category,
            String childAge,
            String title,
            String content,
            String imageUrl
    ) {
        return CommunityPost.builder()
                .userId(userId)
                .category(category)
                .childAge(childAge)
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .build();
    }

    public void update(PostCategory category, String childAge, String title, String content, String imageUrl) {
        this.category = category;
        this.childAge = childAge;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseCommentCount() {
        this.commentCount += 1;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount -= 1;
        }
    }
}
