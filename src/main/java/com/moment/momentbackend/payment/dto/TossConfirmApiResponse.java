package com.moment.momentbackend.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossConfirmApiResponse {

    private String paymentKey;
    private String orderId;
    private String orderName;
    private String method;
    private String status;
    private Integer totalAmount;
    private String approvedAt;
}