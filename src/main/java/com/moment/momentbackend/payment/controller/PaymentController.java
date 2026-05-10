package com.moment.momentbackend.payment.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.payment.dto.PaymentPrepareRequest;
import com.moment.momentbackend.payment.dto.PaymentPrepareResponse;
import com.moment.momentbackend.payment.dto.PaymentResultResponse;
import com.moment.momentbackend.payment.dto.TossPaymentConfirmRequest;
import com.moment.momentbackend.payment.dto.TossPaymentFailRequest;
import com.moment.momentbackend.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/toss/prepare")
    public ApiResponse<PaymentPrepareResponse> prepareTossPayment(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PaymentPrepareRequest request
    ) {
        PaymentPrepareResponse response = paymentService.prepareTossPayment(userId, request);
        return ApiResponse.ok(response, "결제 준비가 완료되었습니다.");
    }

    @PostMapping("/toss/confirm")
    public ApiResponse<PaymentResultResponse> confirmTossPayment(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody TossPaymentConfirmRequest request
    ) {
        PaymentResultResponse response = paymentService.confirmTossPayment(userId, request);
        return ApiResponse.ok(response, "결제가 승인되었습니다.");
    }

    @PostMapping("/toss/fail")
    public ApiResponse<PaymentResultResponse> failTossPayment(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody TossPaymentFailRequest request
    ) {
        PaymentResultResponse response = paymentService.failTossPayment(userId, request);
        return ApiResponse.ok(response, "결제 실패가 처리되었습니다.");
    }
}