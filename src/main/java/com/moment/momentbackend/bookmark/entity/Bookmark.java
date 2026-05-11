package com.moment.momentbackend.bookmark.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "bookmark")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long programId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static Bookmark of(Long userId, Long programId) {
        Bookmark bookmark = new Bookmark();
        bookmark.userId = userId;
        bookmark.programId = programId;
        bookmark.createdAt = LocalDateTime.now();
        return bookmark;
    }
}
