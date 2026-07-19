package com.code81.onlinestore.service.impl;

import com.code81.onlinestore.dto.cart.CartItemRequest;
import com.code81.onlinestore.dto.cart.CartItemUpdateRequest;
import com.code81.onlinestore.dto.cart.CartResponse;
import com.code81.onlinestore.entity.Cart;
import com.code81.onlinestore.entity.CartItem;
import com.code81.onlinestore.entity.Customer;
import com.code81.onlinestore.entity.Product;
import com.code81.onlinestore.exception.InsufficientStockException;
import com.code81.onlinestore.exception.ResourceNotFoundException;
import com.code81.onlinestore.mapper.CartMapper;
import com.code81.onlinestore.repository.CartItemRepository;
import com.code81.onlinestore.repository.CartRepository;
import com.code81.onlinestore.repository.CustomerRepository;
import com.code81.onlinestore.repository.ProductRepository;
import com.code81.onlinestore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CartResponse getCart(Long customerId) {
        return CartMapper.toResponse(getOrCreateCart(customerId));
    }

    @Override
    @Transactional
    public CartResponse addItem(Long customerId, CartItemRequest request) {
        Cart cart = getOrCreateCart(customerId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        if (!product.isActive()) {
            throw new InsufficientStockException("Product '" + product.getName() + "' is no longer available");
        }

        var existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        int newQuantity = existing.map(CartItem::getQuantity).orElse(0) + request.getQuantity();

        // Soft check here for immediate feedback while building the cart; the
        // authoritative, lock-protected check happens again at checkout.
        if (newQuantity > product.getStockQuantity()) {
            throw new InsufficientStockException(
                    "Only " + product.getStockQuantity() + " unit(s) of '" + product.getName() + "' are in stock");
        }

        if (existing.isPresent()) {
            existing.get().setQuantity(newQuantity);
            cartItemRepository.save(existing.get());
        } else {
            cartItemRepository.save(CartItem.builder().cart(cart).product(product).quantity(newQuantity).build());
        }
        touch(cart);
        return CartMapper.toResponse(getOrCreateCart(customerId));
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long customerId, Long productId, CartItemUpdateRequest request) {
        Cart cart = getOrCreateCart(customerId);
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item for product", productId));

        if (request.getQuantity() > item.getProduct().getStockQuantity()) {
            throw new InsufficientStockException(
                    "Only " + item.getProduct().getStockQuantity() + " unit(s) of '" + item.getProduct().getName() + "' are in stock");
        }
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        touch(cart);
        return CartMapper.toResponse(getOrCreateCart(customerId));
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long customerId, Long productId) {
        Cart cart = getOrCreateCart(customerId);
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item for product", productId));
        cartItemRepository.delete(item);
        touch(cart);
        return CartMapper.toResponse(getOrCreateCart(customerId));
    }

    @Override
    @Transactional
    public void clearCart(Long customerId) {
        Cart cart = getOrCreateCart(customerId);
        cart.getItems().clear();
        touch(cart);
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId).orElseGet(() -> {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
            return cartRepository.save(Cart.builder().customer(customer).build());
        });
    }

    private void touch(Cart cart) {
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
    }
}
