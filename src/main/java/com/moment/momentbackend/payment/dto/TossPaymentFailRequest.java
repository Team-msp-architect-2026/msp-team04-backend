package com.moment.momentbackend.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentFailRequest {

    @NotBlank(message = "orderId는 필수입니다.")
    private String orderId;

    private String failureCode;

    private String failureMessage;
}