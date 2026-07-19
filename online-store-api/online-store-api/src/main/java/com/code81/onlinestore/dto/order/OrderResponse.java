package com.code81.onlinestore.dto.order;

import com.code81.onlinestore.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long customerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Long shippingAddressId;
    private List<OrderItemResponse> items;
    private Instant createdAt;
    private Instant updatedAt;
}
