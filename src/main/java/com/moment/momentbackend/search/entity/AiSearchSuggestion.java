package com.moment.momentbackend.search.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ai_search_suggestion")
public class AiSearchSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "suggestion_text", nullable = false, length = 255)
    private String suggestionText;

    @Column(name = "is_global", nullable = false)
    private Boolean isGlobal;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private AiSearchSuggestion(Long userId, String suggestionText, Boolean isGlobal) {
        this.userId = userId;
        this.suggestionText = suggestionText;
        this.isGlobal = isGlobal;
        this.createdAt = LocalDateTime.now();
    }

    public static AiSearchSuggestion createPersonal(Long userId, String suggestionText) {
        return new AiSearchSuggestion(userId, suggestionText, false);
    }

    public static AiSearchSuggestion createGlobal(String suggestionText) {
        return new AiSearchSuggestion(null, suggestionText, true);
    }
}
