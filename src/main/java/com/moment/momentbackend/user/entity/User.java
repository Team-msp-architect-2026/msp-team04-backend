package com.moment.momentbackend.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    @Column
    private String nickname;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public User(Long kakaoId, String nickname, LocalDateTime createdAt) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }
}