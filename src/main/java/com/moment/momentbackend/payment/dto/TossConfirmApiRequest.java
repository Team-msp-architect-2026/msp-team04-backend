package com.moment.momentbackend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TossConfirmApiRequest {

    private String paymentKey;
    private String orderId;
    private Integer amount;
}