package com.code81.onlinestore.controller;

import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.dto.staff.StaffCreateRequest;
import com.code81.onlinestore.dto.staff.StaffResponse;
import com.code81.onlinestore.dto.staff.StaffUpdateRequest;
import com.code81.onlinestore.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Staff", description = "Staff account management (admin only)")
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @Operation(summary = "Create a staff account")
    public ResponseEntity<StaffResponse> create(@Valid @RequestBody StaffCreateRequest request) {
        StaffResponse created = staffService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "List staff accounts")
    public ResponseEntity<PageResponse<StaffResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(staffService.list(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a staff account by id")
    public ResponseEntity<StaffResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a staff account's role and active status")
    public ResponseEntity<StaffResponse> update(@PathVariable Long id, @Valid @RequestBody StaffUpdateRequest request) {
        return ResponseEntity.ok(staffService.update(id, request));
    }
}
