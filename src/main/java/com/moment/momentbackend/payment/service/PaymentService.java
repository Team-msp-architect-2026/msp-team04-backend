package com.moment.momentbackend.payment.service;

import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.application.repository.ApplicationRepository;
import com.moment.momentbackend.application.type.ApplicationStatus;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.payment.client.TossPaymentClient;
import com.moment.momentbackend.payment.dto.*;
import com.moment.momentbackend.payment.entity.Payment;
import com.moment.momentbackend.payment.repository.PaymentRepository;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApplicationRepository applicationRepository;
    private final ProgramRepository programRepository;
    private final TossPaymentClient tossPaymentClient;

    @Value("${toss.client-key:}")
    private String tossClientKey;

    @Value("${toss.success-url:}")
    private String tossSuccessUrl;

    @Value("${toss.fail-url:}")
    private String tossFailUrl;

    @Transactional
    public PaymentPrepareResponse prepareTossPayment(Long userId, PaymentPrepareRequest request) {
        validateAuthenticatedUser(userId);

        Application application = getApplicationOwnedByUser(request.getApplicationId(), userId);

        Program program = programRepository.findById(application.getProgramId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        Optional<Payment> existingPayment = paymentRepository.findByApplicationId(application.getId());
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();

            if (payment.isReady()) {
                return PaymentPrepareResponse.of(
                        payment,
                        application,
                        createOrderName(program),
                        tossClientKey,
                        tossSuccessUrl,
                        tossFailUrl
                );
            }

            throw new CustomException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        validateApplicationPending(application);

        Integer amount = resolvePaymentAmount(program);
        String orderId = createOrderId(application.getId());
        String orderName = createOrderName(program);

        Payment payment;
        if (amount == 0 || Boolean.TRUE.equals(program.getIsFree())) {
            payment = Payment.createApprovedFree(application.getId(), orderId);
            application.changeStatus(ApplicationStatus.CONFIRMED);
        } else {
            payment = Payment.createReadyToss(application.getId(), amount, orderId);
            application.changeStatus(ApplicationStatus.PAYMENT_READY);
        }

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentPrepareResponse.of(
                savedPayment,
                application,
                orderName,
                tossClientKey,
                tossSuccessUrl,
                tossFailUrl
        );
    }

    @Transactional
    public PaymentResultResponse confirmTossPayment(Long userId, TossPaymentConfirmRequest request) {
        validateAuthenticatedUser(userId);

        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        Application application = getApplicationOwnedByUser(payment.getApplicationId(), userId);

        validatePaymentReady(payment);
        validateApplicationPaymentReady(application);
        validateAmount(payment, request.getAmount());
        validatePaymentKeyNotDuplicated(request.getPaymentKey());

        TossConfirmApiResponse tossResponse = tossPaymentClient.confirm(
                new TossConfirmApiRequest(
                        request.getPaymentKey(),
                        request.getOrderId(),
                        request.getAmount()
                )
        );

        if (tossResponse == null || tossResponse.getPaymentKey() == null) {
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        payment.approve(tossResponse.getPaymentKey());
        application.changeStatus(ApplicationStatus.CONFIRMED);

        return PaymentResultResponse.of(payment, application);
    }

    @Transactional
    public PaymentResultResponse failTossPayment(Long userId, TossPaymentFailRequest request) {
        validateAuthenticatedUser(userId);

        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        Application application = getApplicationOwnedByUser(payment.getApplicationId(), userId);

        validatePaymentReady(payment);

        Program program = programRepository.findByIdForUpdate(application.getProgramId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        payment.fail(
                request.getFailureCode() == null ? "PAYMENT_FAILED" : request.getFailureCode(),
                request.getFailureMessage() == null ? "결제가 실패했습니다." : request.getFailureMessage()
        );
        application.changeStatus(ApplicationStatus.FAILED);
        program.restoreRemainCapacity();

        return PaymentResultResponse.of(payment, application);
    }

    private void validateAuthenticatedUser(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    private Application getApplicationOwnedByUser(Long applicationId, Long userId) {
        return applicationRepository.findByIdAndUserId(applicationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    private void validateApplicationPending(Application application) {
        if (application.getApplicationStatus() != ApplicationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_APPLICATION_STATUS);
        }
    }

    private void validateApplicationPaymentReady(Application application) {
        if (application.getApplicationStatus() != ApplicationStatus.PAYMENT_READY) {
            throw new CustomException(ErrorCode.INVALID_APPLICATION_STATUS);
        }
    }

    private void validatePaymentReady(Payment payment) {
        if (!payment.isReady()) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }
    }

    private void validateAmount(Payment payment, Integer requestAmount) {
        if (!payment.getPaymentAmount().equals(requestAmount)) {
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }
    }

    private void validatePaymentKeyNotDuplicated(String paymentKey) {
        if (paymentRepository.existsByPaymentKey(paymentKey)) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }
    }

    private Integer resolvePaymentAmount(Program program) {
        if (Boolean.TRUE.equals(program.getIsFree())) {
            return 0;
        }

        return program.getPrice() == null ? 0 : program.getPrice();
    }

    private String createOrderId(Long applicationId) {
        return "ORDER-" + applicationId + "-" + UUID.randomUUID();
    }

    private String createOrderName(Program program) {
        return program.getTitle();
    }
}