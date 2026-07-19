package com.code81.onlinestore.controller;

import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.dto.order.OrderResponse;
import com.code81.onlinestore.dto.order.OrderStatusUpdateRequest;
import com.code81.onlinestore.dto.order.PlaceOrderRequest;
import com.code81.onlinestore.entity.OrderStatus;
import com.code81.onlinestore.security.AppUserPrincipal;
import com.code81.onlinestore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and management")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Place an order from my current cart")
    public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal AppUserPrincipal principal,
                                                      @Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse created = orderService.placeOrder(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "List my own orders")
    public ResponseEntity<PageResponse<OrderResponse>> listMine(@AuthenticationPrincipal AppUserPrincipal principal,
                                                                  @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(orderService.listForCustomer(principal.getId(), pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER', 'SUPPORT_AGENT')")
    @Operation(summary = "List all orders, optionally filtered by status (staff only)")
    public ResponseEntity<PageResponse<OrderResponse>> listAll(@RequestParam(required = false) OrderStatus status,
                                                                 @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(orderService.listAll(status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order by id (own order for a customer, any order for staff)")
    public ResponseEntity<OrderResponse> getById(@AuthenticationPrincipal AppUserPrincipal principal,
                                                  @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id, principal.getId(), principal.isStaff()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status. Customers may only cancel their own order; " +
            "staff may move it through PAID/SHIPPED/DELIVERED/CANCELLED per the allowed lifecycle.")
    public ResponseEntity<OrderResponse> updateStatus(@AuthenticationPrincipal AppUserPrincipal principal,
                                                        @PathVariable Long id,
                                                        @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse updated = orderService.updateStatus(id, request.getStatus(), principal.getId(), principal.isStaff());
        return ResponseEntity.ok(updated);
    }
}
