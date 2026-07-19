package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.category.CategoryRequest;
import com.code81.onlinestore.dto.category.CategoryResponse;
import com.code81.onlinestore.dto.common.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    CategoryResponse getById(Long id);

    PageResponse<CategoryResponse> list(Pageable pageable);

    void delete(Long id);
}
