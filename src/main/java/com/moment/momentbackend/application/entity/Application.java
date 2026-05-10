package com.moment.momentbackend.application.entity;

import com.moment.momentbackend.application.type.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long childId;

    @Column(nullable = false)
    private Long programId;

    @Column(nullable = false)
    private String applicantName;

    @Column(nullable = false)
    private String parentName;

    @Column(nullable = false)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String requestNote;

    @Column(columnDefinition = "TEXT")
    private String aiStartMessage;

    @Column(nullable = false)
    private Boolean agreeTerms;

    @Column(nullable = false)
    private Boolean agreePrivacy;

    @Column
    private Integer reserveNo;

    @Column
    private LocalDateTime seatLockedUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus applicationStatus;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    @Column
    private LocalDateTime updatedAt;

    @Builder
    private Application(
            Long userId,
            Long childId,
            Long programId,
            String applicantName,
            String parentName,
            String phone,
            String requestNote,
            String aiStartMessage,
            Boolean agreeTerms,
            Boolean agreePrivacy,
            Integer reserveNo,
            LocalDateTime seatLockedUntil,
            ApplicationStatus applicationStatus,
            LocalDateTime appliedAt
    ) {
        this.userId = userId;
        this.childId = childId;
        this.programId = programId;
        this.applicantName = applicantName;
        this.parentName = parentName;
        this.phone = phone;
        this.requestNote = requestNote;
        this.aiStartMessage = aiStartMessage;
        this.agreeTerms = agreeTerms;
        this.agreePrivacy = agreePrivacy;
        this.reserveNo = reserveNo;
        this.seatLockedUntil = seatLockedUntil;
        this.applicationStatus = applicationStatus;
        this.appliedAt = appliedAt;
    }

    public static Application createPending(
            Long userId,
            Long childId,
            Long programId,
            String applicantName,
            String parentName,
            String phone,
            String requestNote,
            String aiStartMessage,
            Boolean agreeTerms,
            Boolean agreePrivacy,
            Integer reserveNo,
            LocalDateTime seatLockedUntil
    ) {
        return Application.builder()
                .userId(userId)
                .childId(childId)
                .programId(programId)
                .applicantName(applicantName)
                .parentName(parentName)
                .phone(phone)
                .requestNote(requestNote)
                .aiStartMessage(aiStartMessage)
                .agreeTerms(agreeTerms)
                .agreePrivacy(agreePrivacy)
                .reserveNo(reserveNo)
                .seatLockedUntil(seatLockedUntil)
                .applicationStatus(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();
    }

    public void changeStatus(ApplicationStatus status) {
        this.applicationStatus = status;
        this.updatedAt = LocalDateTime.now();
    }
}