package com.code81.onlinestore.service.impl;

import com.code81.onlinestore.dto.category.CategoryRequest;
import com.code81.onlinestore.dto.category.CategoryResponse;
import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.entity.Category;
import com.code81.onlinestore.exception.DuplicateResourceException;
import com.code81.onlinestore.exception.ResourceInUseException;
import com.code81.onlinestore.exception.ResourceNotFoundException;
import com.code81.onlinestore.mapper.CategoryMapper;
import com.code81.onlinestore.repository.CategoryRepository;
import com.code81.onlinestore.repository.ProductRepository;
import com.code81.onlinestore.service.ActivityLogService;
import com.code81.onlinestore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("A category named '" + request.getName() + "' already exists");
        }
        Category saved = categoryRepository.save(CategoryMapper.toEntity(request));
        activityLogService.log("CREATE_CATEGORY", "Category", saved.getId(), "name=" + saved.getName());
        return CategoryMapper.toResponse(saved, 0);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findEntity(id);
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("A category named '" + request.getName() + "' already exists");
        }
        CategoryMapper.updateEntity(category, request);
        Category saved = categoryRepository.save(category);
        activityLogService.log("UPDATE_CATEGORY", "Category", saved.getId(), "name=" + saved.getName());
        return CategoryMapper.toResponse(saved, productRepository.countByCategoryId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = findEntity(id);
        return CategoryMapper.toResponse(category, productRepository.countByCategoryId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> list(Pageable pageable) {
        Page<CategoryResponse> page = categoryRepository.findAll(pageable)
                .map(category -> CategoryMapper.toResponse(category, productRepository.countByCategoryId(category.getId())));
        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = findEntity(id);
        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new ResourceInUseException(
                    "Cannot delete category '" + category.getName() + "': " + productCount + " product(s) still reference it");
        }
        categoryRepository.delete(category);
        activityLogService.log("DELETE_CATEGORY", "Category", id, "name=" + category.getName());
    }

    private Category findEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }
}
