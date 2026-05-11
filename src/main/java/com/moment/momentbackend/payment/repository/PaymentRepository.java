package com.moment.momentbackend.payment.repository;

import com.moment.momentbackend.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findByApplicationId(Long applicationId);

    boolean existsByApplicationId(Long applicationId);

    boolean existsByPaymentKey(String paymentKey);
}
