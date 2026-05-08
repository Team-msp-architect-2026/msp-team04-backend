package com.moment.momentbackend.child.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "child_profile")
public class ChildProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String childName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "childProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChildConcern> concerns = new ArrayList<>();

    @Builder
    public ChildProfile(Long userId, String childName, LocalDate birthDate, LocalDateTime createdAt) {
        this.userId = userId;
        this.childName = childName;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
    }

    public void update(String childName, LocalDate birthDate) {
        this.childName = childName;
        this.birthDate = birthDate;
        this.updatedAt = LocalDateTime.now();
    }
}