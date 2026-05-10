package com.moment.momentbackend.payment.entity;

import com.moment.momentbackend.payment.type.PaymentMethod;
import com.moment.momentbackend.payment.type.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long applicationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private Integer paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private String orderId;

    @Column
    private String paymentKey;

    @Column
    private String failureCode;

    @Column(columnDefinition = "TEXT")
    private String failureMessage;

    @Column
    private LocalDateTime approvedAt;

    @Column
    private LocalDateTime cancelledAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Builder
    private Payment(
            Long applicationId,
            PaymentMethod paymentMethod,
            Integer paymentAmount,
            PaymentStatus paymentStatus,
            String orderId,
            String paymentKey,
            String failureCode,
            String failureMessage,
            LocalDateTime approvedAt,
            LocalDateTime cancelledAt,
            LocalDateTime createdAt
    ) {
        this.applicationId = applicationId;
        this.paymentMethod = paymentMethod;
        this.paymentAmount = paymentAmount;
        this.paymentStatus = paymentStatus;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
        this.approvedAt = approvedAt;
        this.cancelledAt = cancelledAt;
        this.createdAt = createdAt;
    }

    public static Payment createReadyToss(Long applicationId, Integer paymentAmount, String orderId) {
        return Payment.builder()
                .applicationId(applicationId)
                .paymentMethod(PaymentMethod.TOSS_PAYMENTS)
                .paymentAmount(paymentAmount)
                .paymentStatus(PaymentStatus.READY)
                .orderId(orderId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Payment createApprovedFree(Long applicationId, String orderId) {
        LocalDateTime now = LocalDateTime.now();

        return Payment.builder()
                .applicationId(applicationId)
                .paymentMethod(PaymentMethod.FREE)
                .paymentAmount(0)
                .paymentStatus(PaymentStatus.APPROVED)
                .orderId(orderId)
                .approvedAt(now)
                .createdAt(now)
                .build();
    }

    public boolean isReady() {
        return this.paymentStatus == PaymentStatus.READY;
    }

    public boolean isApproved() {
        return this.paymentStatus == PaymentStatus.APPROVED;
    }

    public void approve(String paymentKey) {
        this.paymentKey = paymentKey;
        this.paymentStatus = PaymentStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void fail(String failureCode, String failureMessage) {
        this.paymentStatus = PaymentStatus.FAILED;
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String failureCode, String failureMessage) {
        this.paymentStatus = PaymentStatus.CANCELLED;
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
        this.cancelledAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void expire(String failureCode, String failureMessage) {
        this.paymentStatus = PaymentStatus.EXPIRED;
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
        this.updatedAt = LocalDateTime.now();
    }
}