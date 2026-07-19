package com.code81.onlinestore.controller;

import com.code81.onlinestore.dto.cart.CartItemRequest;
import com.code81.onlinestore.dto.cart.CartItemUpdateRequest;
import com.code81.onlinestore.dto.cart.CartResponse;
import com.code81.onlinestore.security.AppUserPrincipal;
import com.code81.onlinestore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Cart", description = "Shopping cart for the authenticated customer")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get my cart")
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(cartService.getCart(principal.getId()));
    }

    @PostMapping("/items")
    @Operation(summary = "Add a product to my cart (merges quantity if already present)")
    public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal AppUserPrincipal principal,
                                                 @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(principal.getId(), request));
    }

    @PutMapping("/items/{productId}")
    @Operation(summary = "Set the exact quantity for a product already in my cart")
    public ResponseEntity<CartResponse> updateItem(@AuthenticationPrincipal AppUserPrincipal principal,
                                                    @PathVariable Long productId,
                                                    @Valid @RequestBody CartItemUpdateRequest request) {
        return ResponseEntity.ok(cartService.updateItem(principal.getId(), productId, request));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove a product from my cart")
    public ResponseEntity<CartResponse> removeItem(@AuthenticationPrincipal AppUserPrincipal principal,
                                                    @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(principal.getId(), productId));
    }

    @DeleteMapping
    @Operation(summary = "Empty my cart")
    public ResponseEntity<Void> clear(@AuthenticationPrincipal AppUserPrincipal principal) {
        cartService.clearCart(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
