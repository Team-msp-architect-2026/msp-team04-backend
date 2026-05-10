package com.moment.momentbackend.payment.dto;

import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentPrepareResponse {

    private Long paymentId;
    private Long applicationId;
    private String orderId;
    private String orderName;
    private Integer amount;
    private String clientKey;
    private String successUrl;
    private String failUrl;
    private String paymentMethod;
    private String paymentStatus;
    private String applicationStatus;

    public static PaymentPrepareResponse of(
            Payment payment,
            Application application,
            String orderName,
            String clientKey,
            String successUrl,
            String failUrl
    ) {
        return PaymentPrepareResponse.builder()
                .paymentId(payment.getId())
                .applicationId(payment.getApplicationId())
                .orderId(payment.getOrderId())
                .orderName(orderName)
                .amount(payment.getPaymentAmount())
                .clientKey(clientKey)
                .successUrl(successUrl)
                .failUrl(failUrl)
                .paymentMethod(payment.getPaymentMethod().name())
                .paymentStatus(payment.getPaymentStatus().name())
                .applicationStatus(application.getApplicationStatus().name())
                .build();
    }
}