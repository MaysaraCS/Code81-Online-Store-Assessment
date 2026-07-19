package com.code81.onlinestore.mapper;

import com.code81.onlinestore.dto.category.CategoryRequest;
import com.code81.onlinestore.dto.category.CategoryResponse;
import com.code81.onlinestore.entity.Category;

/**
 * Manual, explicit mapping (no MapStruct/ModelMapper) so the field-by-field
 * behaviour is easy to read and step through during a demo. Swap for
 * MapStruct later if the DTO surface grows a lot.
 */
public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public static void updateEntity(Category category, CategoryRequest request) {
        category.setName(request.getName());
        category.setDescription(request.getDescription());
    }

    public static CategoryResponse toResponse(Category category, long productCount) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .productCount(productCount)
                .build();
    }
}
