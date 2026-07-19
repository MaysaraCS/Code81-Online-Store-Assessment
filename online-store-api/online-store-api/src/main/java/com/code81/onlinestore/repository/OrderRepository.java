package com.code81.onlinestore.repository;

import com.code81.onlinestore.entity.OrderStatus;
import com.code81.onlinestore.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    Page<Orders> findByCustomerId(Long customerId, Pageable pageable);

    Page<Orders> findByStatus(OrderStatus status, Pageable pageable);
}
