package com.code81.onlinestore.repository;

import com.code81.onlinestore.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByStripeCheckoutSessionId(String sessionId);
}
