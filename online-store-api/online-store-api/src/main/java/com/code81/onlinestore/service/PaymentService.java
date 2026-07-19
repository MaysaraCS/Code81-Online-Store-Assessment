package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.payment.CheckoutSessionResponse;

public interface PaymentService {

    /** Starts a Stripe Checkout Session for an order the caller owns, provided it's still PLACED. */
    CheckoutSessionResponse createCheckoutSession(Long orderId, Long customerId);

    /** Called by the webhook handler once Stripe confirms the checkout session was paid. */
    void handleCheckoutCompleted(String stripeCheckoutSessionId, String stripePaymentIntentId);
}
