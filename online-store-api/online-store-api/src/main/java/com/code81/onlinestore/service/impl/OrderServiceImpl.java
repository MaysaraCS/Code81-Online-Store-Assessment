package com.code81.onlinestore.service.impl;

import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.dto.order.OrderResponse;
import com.code81.onlinestore.dto.order.PlaceOrderRequest;
import com.code81.onlinestore.entity.*;
import com.code81.onlinestore.exception.*;
import com.code81.onlinestore.mapper.OrderMapper;
import com.code81.onlinestore.repository.*;
import com.code81.onlinestore.service.ActivityLogService;
import com.code81.onlinestore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final ActivityLogService activityLogService;

    /**
     * Explicit state machine for the order lifecycle. Anything not listed
     * here (e.g. DELIVERED -> anything, CANCELLED -> anything) is terminal.
     */
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.PLACED, EnumSet.of(OrderStatus.PAID, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.PAID, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED));
        ALLOWED_TRANSITIONS.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(Long customerId, PlaceOrderRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        Address address = addressRepository.findByIdAndCustomerId(request.getAddressId(), customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", request.getAddressId()));

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalStateException("Your cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Your cart is empty");
        }

        // Lock product rows in a stable order (ascending product id) across all
        // checkouts, so two concurrent checkouts that share a product can never
        // deadlock waiting on each other's locks.
        List<CartItem> sortedItems = cart.getItems().stream()
                .sorted(Comparator.comparing(ci -> ci.getProduct().getId()))
                .toList();

        Orders order = Orders.builder()
                .customer(customer)
                .shippingAddress(address)
                .status(OrderStatus.PLACED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : sortedItems) {
            Product product = productRepository.findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", cartItem.getProduct().getId()));

            if (!product.isActive()) {
                throw new InsufficientStockException("Product '" + product.getName() + "' is no longer available");
            }
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Only " + product.getStockQuantity() + " unit(s) of '" + product.getName() + "' are in stock");
            }

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(subtotal);

            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build());
        }
        order.setTotalAmount(total);

        Orders saved = orderRepository.save(order);

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);

        return OrderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId, Long requesterCustomerId, boolean requesterIsStaff) {
        Orders order = findEntity(orderId);
        assertOwnershipOrStaff(order, requesterCustomerId, requesterIsStaff);
        return OrderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> listForCustomer(Long customerId, Pageable pageable) {
        Page<OrderResponse> page = orderRepository.findByCustomerId(customerId, pageable).map(OrderMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> listAll(OrderStatus statusFilter, Pageable pageable) {
        Page<Orders> page = statusFilter == null
                ? orderRepository.findAll(pageable)
                : orderRepository.findByStatus(statusFilter, pageable);
        return PageResponse.from(page.map(OrderMapper::toResponse));
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus, Long requesterCustomerId, boolean requesterIsStaff) {
        Orders order = findEntity(orderId);
        assertOwnershipOrStaff(order, requesterCustomerId, requesterIsStaff);

        // Customers may only ever request a CANCELLED transition on their own
        // order; every other target status is a staff-only action, enforced
        // again here (not just at the controller) since this method is the
        // single choke point both paths go through.
        if (!requesterIsStaff && newStatus != OrderStatus.CANCELLED) {
            throw new ForbiddenOperationException("Customers may only cancel an order, not set it to " + newStatus);
        }

        OrderStatus current = order.getStatus();
        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(OrderStatus.class));
        if (!allowed.contains(newStatus)) {
            throw new InvalidOrderStatusTransitionException(
                    "Cannot move an order from " + current + " to " + newStatus);
        }

        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(newStatus);
        Orders saved = orderRepository.save(order);

        if (requesterIsStaff) {
            activityLogService.log("UPDATE_ORDER_STATUS", "Order", saved.getId(),
                    current + " -> " + newStatus);
        }

        return OrderMapper.toResponse(saved);
    }

    private void restoreStock(Orders order) {
        List<OrderItem> sortedItems = order.getItems().stream()
                .sorted(Comparator.comparing(oi -> oi.getProduct().getId()))
                .toList();
        for (OrderItem item : sortedItems) {
            Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", item.getProduct().getId()));
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
    }

    private void assertOwnershipOrStaff(Orders order, Long requesterCustomerId, boolean requesterIsStaff) {
        if (!requesterIsStaff && !order.getCustomer().getId().equals(requesterCustomerId)) {
            throw new ForbiddenOperationException("You do not have access to this order");
        }
    }

    private Orders findEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }
}
