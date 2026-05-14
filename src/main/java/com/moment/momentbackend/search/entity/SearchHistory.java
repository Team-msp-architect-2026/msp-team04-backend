package com.moment.momentbackend.search.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "search_history")
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String keyword;

    @Column(name = "searched_at", nullable = false)
    private LocalDateTime searchedAt;

    private SearchHistory(Long userId, String keyword) {
        this.userId = userId;
        this.keyword = keyword;
        this.searchedAt = LocalDateTime.now();
    }

    public static SearchHistory create(Long userId, String keyword) {
        return new SearchHistory(userId, keyword);
    }
}
