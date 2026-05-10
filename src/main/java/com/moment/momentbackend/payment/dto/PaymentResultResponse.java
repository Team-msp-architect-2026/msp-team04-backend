package com.moment.momentbackend.payment.dto;

import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResultResponse {

    private Long paymentId;
    private Long applicationId;
    private String orderId;
    private Integer amount;
    private String paymentMethod;
    private String paymentStatus;
    private String applicationStatus;
    private String failureCode;
    private String failureMessage;
    private LocalDateTime approvedAt;
    private LocalDateTime cancelledAt;

    public static PaymentResultResponse of(Payment payment, Application application) {
        return PaymentResultResponse.builder()
                .paymentId(payment.getId())
                .applicationId(payment.getApplicationId())
                .orderId(payment.getOrderId())
                .amount(payment.getPaymentAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .paymentStatus(payment.getPaymentStatus().name())
                .applicationStatus(application.getApplicationStatus().name())
                .failureCode(payment.getFailureCode())
                .failureMessage(payment.getFailureMessage())
                .approvedAt(payment.getApprovedAt())
                .cancelledAt(payment.getCancelledAt())
                .build();
    }
}