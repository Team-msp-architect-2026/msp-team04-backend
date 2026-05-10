package com.moment.momentbackend.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPrepareRequest {

    @NotNull(message = "신청 ID는 필수입니다.")
    private Long applicationId;
}