package com.code81.onlinestore.controller;

import com.code81.onlinestore.dto.category.CategoryRequest;
import com.code81.onlinestore.dto.category.CategoryResponse;
import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * NOTE: no @PreAuthorize yet - role-based protection (STORE_MANAGER/ADMIN only
 * for write operations) is wired in Phase 3 once Spring Security is in place.
 * All endpoints here are open for now so we can exercise them from Postman.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product category management")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "List categories (paged)")
    public ResponseEntity<PageResponse<CategoryResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(categoryService.list(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by id")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a category (staff only, once security is added)")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse created = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category (staff only, once security is added)")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category; blocked if it still has products")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
