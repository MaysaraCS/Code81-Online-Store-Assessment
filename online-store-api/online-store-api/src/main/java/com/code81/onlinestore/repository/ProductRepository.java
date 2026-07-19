package com.code81.onlinestore.repository;

import com.code81.onlinestore.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

    long countByCategoryId(Long categoryId);

    /**
     * Row-level lock held for the rest of the transaction. Used when placing
     * or cancelling an order so two concurrent checkouts on the same product
     * can't both read the same stock count and both succeed when only one
     * should - the second transaction blocks here until the first commits.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(Long id);
}
