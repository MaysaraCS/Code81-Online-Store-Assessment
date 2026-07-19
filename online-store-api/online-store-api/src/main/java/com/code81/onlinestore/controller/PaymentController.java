package com.code81.onlinestore.controller;

import com.code81.onlinestore.dto.payment.CheckoutSessionResponse;
import com.code81.onlinestore.security.AppUserPrincipal;
import com.code81.onlinestore.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Stripe Checkout session creation and webhook")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    @Value("${app.stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/api/orders/{orderId}/payment/checkout-session")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Start a Stripe Checkout session for one of my own orders (must be PLACED)")
    public ResponseEntity<CheckoutSessionResponse> createCheckoutSession(
            @AuthenticationPrincipal AppUserPrincipal principal, @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.createCheckoutSession(orderId, principal.getId()));
    }

    /**
     * Public on purpose - Stripe calls this, not a logged-in user. Trust
     * comes from verifying the signature below, not from a JWT.
     * See README "Testing Stripe locally" for how to point Stripe at this
     * endpoint during development using the Stripe CLI.
     */
    @PostMapping("/api/payments/webhook")
    @Operation(summary = "Stripe webhook receiver (called by Stripe, not by the frontend)")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                 @RequestHeader("Stripe-Signature") String signatureHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Rejected webhook call with invalid Stripe signature");
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            JsonObject sessionJson = JsonParser.parseString(payload)
                    .getAsJsonObject()
                    .getAsJsonObject("data")
                    .getAsJsonObject("object");

            String sessionId = sessionJson.get("id").getAsString();
            String paymentIntentId = (sessionJson.has("payment_intent") && !sessionJson.get("payment_intent").isJsonNull())
                    ? sessionJson.get("payment_intent").getAsString()
                    : null;

            log.info("Processing checkout.session.completed for session {}", sessionId);
            paymentService.handleCheckoutCompleted(sessionId, paymentIntentId);
        }
        // Any other event type: acknowledge and ignore. Stripe retries on
        // non-2xx responses, so we always return 200 once signature is valid.
        return ResponseEntity.ok("received");
    }
}
