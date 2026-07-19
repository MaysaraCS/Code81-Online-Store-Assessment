package com.code81.onlinestore.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CheckoutSessionResponse {
    private String sessionId;
    private String checkoutUrl;
}
