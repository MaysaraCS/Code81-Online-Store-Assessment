package com.code81.onlinestore.controller;

import com.code81.onlinestore.dto.customer.ChangePasswordRequest;
import com.code81.onlinestore.dto.customer.CustomerProfileUpdateRequest;
import com.code81.onlinestore.dto.customer.CustomerRegisterRequest;
import com.code81.onlinestore.dto.customer.CustomerResponse;
import com.code81.onlinestore.security.AppUserPrincipal;
import com.code81.onlinestore.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Every "/me" endpoint below always operates on the id embedded in the
 * caller's JWT (AppUserPrincipal) - never on a client-supplied id - so a
 * customer can never read or edit another customer's profile.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer registration and self-service profile")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    @Operation(summary = "Register a new customer account (public)")
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody CustomerRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.register(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get the authenticated customer's own profile")
    public ResponseEntity<CustomerResponse> getMyProfile(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(customerService.getProfile(principal.getId()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Update the authenticated customer's own profile")
    public ResponseEntity<CustomerResponse> updateMyProfile(@AuthenticationPrincipal AppUserPrincipal principal,
                                                              @Valid @RequestBody CustomerProfileUpdateRequest request) {
        return ResponseEntity.ok(customerService.updateProfile(principal.getId(), request));
    }

    @PostMapping("/me/change-password")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Change the authenticated customer's own password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal AppUserPrincipal principal,
                                                @Valid @RequestBody ChangePasswordRequest request) {
        customerService.changePassword(principal.getId(), request);
        return ResponseEntity.noContent().build();
    }
}
