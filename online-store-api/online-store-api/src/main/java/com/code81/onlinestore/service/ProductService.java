package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.dto.product.ProductRequest;
import com.code81.onlinestore.dto.product.ProductResponse;
import com.code81.onlinestore.dto.product.StockAdjustmentRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    ProductResponse getById(Long id);

    PageResponse<ProductResponse> list(Long categoryId, Boolean active, String search,
                                        BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    ProductResponse adjustStock(Long id, StockAdjustmentRequest request);

    void deactivate(Long id);
}
