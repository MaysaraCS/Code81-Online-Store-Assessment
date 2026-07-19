package com.code81.onlinestore.service.impl;

import com.code81.onlinestore.dto.payment.CheckoutSessionResponse;
import com.code81.onlinestore.entity.OrderStatus;
import com.code81.onlinestore.entity.Orders;
import com.code81.onlinestore.entity.Payment;
import com.code81.onlinestore.entity.PaymentStatus;
import com.code81.onlinestore.exception.ForbiddenOperationException;
import com.code81.onlinestore.exception.PaymentException;
import com.code81.onlinestore.exception.ResourceNotFoundException;
import com.code81.onlinestore.repository.OrderRepository;
import com.code81.onlinestore.repository.PaymentRepository;
import com.code81.onlinestore.service.OrderService;
import com.code81.onlinestore.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Value("${app.stripe.success-url}")
    private String successUrl;

    @Value("${app.stripe.cancel-url}")
    private String cancelUrl;

    @Override
    @Transactional
    public CheckoutSessionResponse createCheckoutSession(Long orderId, Long customerId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new ForbiddenOperationException("You do not have access to this order");
        }
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException(
                    "Only orders in PLACED status can be paid for (current status: " + order.getStatus() + ")");
        }

        // Stripe wants the amount in the smallest currency unit (cents for USD).
        long amountInCents = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValueExact();

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl)
                    .putMetadata("orderId", String.valueOf(order.getId()))
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(amountInCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Online Store order #" + order.getId())
                                                                    .build())
                                                    .build())
                                    .build())
                    .build();

            Session session = Session.create(params);

            Payment payment = Payment.builder()
                    .order(order)
                    .stripeCheckoutSessionId(session.getId())
                    .amount(order.getTotalAmount())
                    .currency("usd")
                    .status(PaymentStatus.PENDING)
                    .build();
            paymentRepository.save(payment);

            return CheckoutSessionResponse.builder()
                    .sessionId(session.getId())
                    .checkoutUrl(session.getUrl())
                    .build();

        } catch (StripeException e) {
            throw new PaymentException("Failed to create Stripe checkout session: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void handleCheckoutCompleted(String stripeCheckoutSessionId, String stripePaymentIntentId) {
        Payment payment = paymentRepository.findByStripeCheckoutSessionId(stripeCheckoutSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for Stripe session", stripeCheckoutSessionId));

        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setStripePaymentIntentId(stripePaymentIntentId);
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);

        // requesterIsStaff=true here isn't "impersonating staff" - it's the
        // documented way updateStatus() lets a trusted server-side caller
        // (this webhook, not an end user) skip the ownership check, since
        // there is no customer/staff HTTP principal involved in a webhook call.
        orderService.updateStatus(payment.getOrder().getId(), OrderStatus.PAID, null, true);
    }
}
