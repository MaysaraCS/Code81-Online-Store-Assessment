package com.code81.onlinestore.mapper;

import com.code81.onlinestore.dto.cart.CartItemResponse;
import com.code81.onlinestore.dto.cart.CartResponse;
import com.code81.onlinestore.entity.Cart;
import com.code81.onlinestore.entity.CartItem;

import java.math.BigDecimal;

public final class CartMapper {

    private CartMapper() {
    }

    public static CartItemResponse toItemResponse(CartItem item) {
        BigDecimal unitPrice = item.getProduct().getPrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .unitPrice(unitPrice)
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .availableStock(item.getProduct().getStockQuantity())
                .build();
    }

    public static CartResponse toResponse(Cart cart) {
        var itemResponses = cart.getItems().stream().map(CartMapper::toItemResponse).toList();
        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalAmount(total)
                .build();
    }
}
