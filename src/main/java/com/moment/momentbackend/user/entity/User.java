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
    private String kakaoId;

    @Column(nullable = false)
    private String parentName;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private String profileImage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Builder
    public User(String kakaoId, String parentName, LocalDateTime createdAt) {
        this.kakaoId = kakaoId;
        this.parentName = parentName;
        this.createdAt = createdAt;
    }
}