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
@Table(name = "comment_like")
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long commentId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private CommentLike(Long commentId, Long userId) {
        this.commentId = commentId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    public static CommentLike create(Long commentId, Long userId) {
        return CommentLike.builder()
                .commentId(commentId)
                .userId(userId)
                .build();
    }
}
