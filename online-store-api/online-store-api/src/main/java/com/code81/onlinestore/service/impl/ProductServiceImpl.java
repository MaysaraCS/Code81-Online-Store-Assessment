package com.code81.onlinestore.service.impl;

import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.dto.product.ProductRequest;
import com.code81.onlinestore.dto.product.ProductResponse;
import com.code81.onlinestore.dto.product.StockAdjustmentRequest;
import com.code81.onlinestore.entity.Category;
import com.code81.onlinestore.entity.Product;
import com.code81.onlinestore.exception.DuplicateResourceException;
import com.code81.onlinestore.exception.ResourceNotFoundException;
import com.code81.onlinestore.mapper.ProductMapper;
import com.code81.onlinestore.repository.CategoryRepository;
import com.code81.onlinestore.repository.ProductRepository;
import com.code81.onlinestore.repository.ProductSpecifications;
import com.code81.onlinestore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySkuIgnoreCase(request.getSku())) {
            throw new DuplicateResourceException("A product with SKU '" + request.getSku() + "' already exists");
        }
        Category category = findCategory(request.getCategoryId());
        Product saved = productRepository.save(ProductMapper.toEntity(request, category));
        return ProductMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findEntity(id);
        if (productRepository.existsBySkuIgnoreCaseAndIdNot(request.getSku(), id)) {
            throw new DuplicateResourceException("A product with SKU '" + request.getSku() + "' already exists");
        }
        Category category = findCategory(request.getCategoryId());
        ProductMapper.updateEntity(product, request, category);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return ProductMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> list(Long categoryId, Boolean active, String search,
                                               BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = Specification.allOf(
                ProductSpecifications.hasCategoryId(categoryId),
                ProductSpecifications.isActive(active),
                ProductSpecifications.nameContains(search),
                ProductSpecifications.priceGreaterThanOrEqual(minPrice),
                ProductSpecifications.priceLessThanOrEqual(maxPrice)
        );
        Page<ProductResponse> page = productRepository.findAll(spec, pageable).map(ProductMapper::toResponse);
        return PageResponse.from(page);
    }
    @Override
    @Transactional
    public ProductResponse adjustStock(Long id, StockAdjustmentRequest request) {
        Product product = findEntity(id);
        int newQuantity = product.getStockQuantity() + request.getDelta();
        if (newQuantity < 0) {
            throw new IllegalArgumentException(
                    "Stock adjustment would result in negative stock (current: " + product.getStockQuantity()
                            + ", delta: " + request.getDelta() + ")");
        }
        product.setStockQuantity(newQuantity);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Product product = findEntity(id);
        product.setActive(false);
        productRepository.save(product);
    }

    private Product findEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
    }
}
