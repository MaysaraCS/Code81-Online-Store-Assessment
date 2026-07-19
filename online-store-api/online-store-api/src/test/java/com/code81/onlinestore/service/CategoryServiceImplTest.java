package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.category.CategoryRequest;
import com.code81.onlinestore.dto.category.CategoryResponse;
import com.code81.onlinestore.entity.Category;
import com.code81.onlinestore.exception.DuplicateResourceException;
import com.code81.onlinestore.exception.ResourceInUseException;
import com.code81.onlinestore.exception.ResourceNotFoundException;
import com.code81.onlinestore.repository.CategoryRepository;
import com.code81.onlinestore.repository.ProductRepository;
import com.code81.onlinestore.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private com.code81.onlinestore.service.ActivityLogService activityLogService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category electronics;

    @BeforeEach
    void setUp() {
        electronics = Category.builder().id(1L).name("Electronics").description("Gadgets").build();
    }

    @Test
    void create_savesCategory_whenNameIsUnique() {
        CategoryRequest request = new CategoryRequest("Electronics", "Gadgets");
        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(electronics);

        CategoryResponse response = categoryService.create(request);

        assertThat(response.getName()).isEqualTo("Electronics");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void create_throwsDuplicateResourceException_whenNameAlreadyExists() {
        CategoryRequest request = new CategoryRequest("Electronics", "Gadgets");
        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(request))
                .isInstanceOf(DuplicateResourceException.class);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getById_throwsResourceNotFoundException_whenMissing() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_throwsResourceInUseException_whenCategoryHasProducts() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(productRepository.countByCategoryId(1L)).thenReturn(3L);

        assertThatThrownBy(() -> categoryService.delete(1L))
                .isInstanceOf(ResourceInUseException.class);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void delete_removesCategory_whenNoProductsReferenceIt() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(productRepository.countByCategoryId(1L)).thenReturn(0L);

        categoryService.delete(1L);

        verify(categoryRepository).delete(electronics);
    }
}
