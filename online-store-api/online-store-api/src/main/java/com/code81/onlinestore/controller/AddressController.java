package com.code81.onlinestore.controller;

import com.code81.onlinestore.dto.address.AddressRequest;
import com.code81.onlinestore.dto.address.AddressResponse;
import com.code81.onlinestore.security.AppUserPrincipal;
import com.code81.onlinestore.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/me/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Addresses", description = "Shipping addresses for the authenticated customer")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "List my addresses (default first)")
    public ResponseEntity<List<AddressResponse>> list(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(addressService.list(principal.getId()));
    }

    @PostMapping
    @Operation(summary = "Add a new address")
    public ResponseEntity<AddressResponse> add(@AuthenticationPrincipal AppUserPrincipal principal,
                                                @Valid @RequestBody AddressRequest request) {
        AddressResponse created = addressService.add(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update one of my addresses")
    public ResponseEntity<AddressResponse> update(@AuthenticationPrincipal AppUserPrincipal principal,
                                                   @PathVariable Long addressId,
                                                   @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.update(principal.getId(), addressId, request));
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete one of my addresses")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AppUserPrincipal principal,
                                        @PathVariable Long addressId) {
        addressService.delete(principal.getId(), addressId);
        return ResponseEntity.noContent().build();
    }
}
