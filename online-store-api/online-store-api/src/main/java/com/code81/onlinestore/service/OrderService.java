package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.dto.order.OrderResponse;
import com.code81.onlinestore.dto.order.PlaceOrderRequest;
import com.code81.onlinestore.entity.OrderStatus;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse placeOrder(Long customerId, PlaceOrderRequest request);

    OrderResponse getOrder(Long orderId, Long requesterCustomerId, boolean requesterIsStaff);

    PageResponse<OrderResponse> listForCustomer(Long customerId, Pageable pageable);

    PageResponse<OrderResponse> listAll(OrderStatus statusFilter, Pageable pageable);

    OrderResponse updateStatus(Long orderId, OrderStatus newStatus, Long requesterCustomerId, boolean requesterIsStaff);
}
