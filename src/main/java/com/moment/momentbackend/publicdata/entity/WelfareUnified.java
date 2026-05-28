package com.moment.momentbackend.publicdata.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "welfare_unified")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WelfareUnified {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private String originalId;
    private String serviceId;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String targetGroup;

    @Column(columnDefinition = "TEXT")
    private String supportType;

    @Column(columnDefinition = "TEXT")
    private String applyMethod;

    @Column(columnDefinition = "TEXT")
    private String applyUrl;

    @Column(columnDefinition = "TEXT")
    private String department;

    @Column(columnDefinition = "TEXT")
    private String localGovName;

    private Boolean isLocal;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}