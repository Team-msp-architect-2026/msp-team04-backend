package com.moment.momentbackend.program.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "program")
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String programType;

    @Column
    private Integer targetAgeMin;

    @Column
    private Integer targetAgeMax;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Boolean isFree;

    @Column
    private String region;

    @Column
    private String detailAddress;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column
    private LocalDate operationStart;

    @Column
    private LocalDate operationEnd;

    @Column
    private String classTime;

    @Column
    private String classType;

    @Column
    private Integer maxCapacity;

    @Column
    private Integer remainCapacity;

    @Column(nullable = false)
    private Boolean isRecruiting;

    @Column
    private LocalDate deadlineDate;

    @Column(nullable = false)
    private BigDecimal ratingAvg;

    @Column(nullable = false)
    private Integer reviewCount;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String curriculum;

    @Column
    private String contactPhone;

    @Column
    private String contactUrl;

    @Column
    private String externalSource;

    @Column
    private String externalId;

    @Column
    private LocalDateTime lastSyncedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramTag> tags = new ArrayList<>();
}