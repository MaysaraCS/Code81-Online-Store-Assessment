package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.cart.CartItemRequest;
import com.code81.onlinestore.dto.cart.CartItemUpdateRequest;
import com.code81.onlinestore.dto.cart.CartResponse;

public interface CartService {

    CartResponse getCart(Long customerId);

    CartResponse addItem(Long customerId, CartItemRequest request);

    CartResponse updateItem(Long customerId, Long productId, CartItemUpdateRequest request);

    CartResponse removeItem(Long customerId, Long productId);

    void clearCart(Long customerId);
}
