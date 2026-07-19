package com.code81.onlinestore.mapper;

import com.code81.onlinestore.dto.order.OrderItemResponse;
import com.code81.onlinestore.dto.order.OrderResponse;
import com.code81.onlinestore.entity.OrderItem;
import com.code81.onlinestore.entity.Orders;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    public static OrderResponse toResponse(Orders order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddressId(order.getShippingAddress().getId())
                .items(order.getItems().stream().map(OrderMapper::toItemResponse).toList())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
