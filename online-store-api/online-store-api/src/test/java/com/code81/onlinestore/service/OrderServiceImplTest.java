package com.code81.onlinestore.service;

import com.code81.onlinestore.entity.*;
import com.code81.onlinestore.exception.ForbiddenOperationException;
import com.code81.onlinestore.exception.InvalidOrderStatusTransitionException;
import com.code81.onlinestore.repository.*;
import com.code81.onlinestore.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private ProductRepository productRepository;
    @Mock private com.code81.onlinestore.service.ActivityLogService activityLogService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer customer;
    private Orders order;

    @BeforeEach
    void setUp() {
        customer = Customer.builder().id(1L).firstName("Jane").lastName("Doe").email("jane@x.com").build();
        Product product = Product.builder().id(10L).name("Mouse").price(BigDecimal.TEN).stockQuantity(5).active(true).build();
        OrderItem item = OrderItem.builder().product(product).quantity(2).unitPrice(BigDecimal.TEN).subtotal(BigDecimal.valueOf(20)).build();
        order = Orders.builder()
                .id(100L)
                .customer(customer)
                .shippingAddress(Address.builder().id(5L).customer(customer).build())
                .status(OrderStatus.PLACED)
                .totalAmount(BigDecimal.valueOf(20))
                .build();
        order.getItems().add(item);
    }

    @Test
    void updateStatus_allowsPlacedToPaid_whenRequesterIsStaff() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = orderService.updateStatus(100L, OrderStatus.PAID, null, true);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void updateStatus_rejectsShippedToPlaced_asInvalidTransition() {
        order.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(100L, OrderStatus.PLACED, null, true))
                .isInstanceOf(InvalidOrderStatusTransitionException.class);
    }

    @Test
    void updateStatus_rejectsCustomerSettingNonCancelledStatus() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(100L, OrderStatus.SHIPPED, 1L, false))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void updateStatus_rejectsAccessToAnotherCustomersOrder() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(100L, OrderStatus.CANCELLED, 999L, false))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void updateStatus_restoresStock_whenCustomerCancelsOwnOrder() {
        Product product = order.getItems().get(0).getProduct();
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(productRepository.findByIdForUpdate(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));

        int stockBefore = product.getStockQuantity();
        var response = orderService.updateStatus(100L, OrderStatus.CANCELLED, 1L, false);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(product.getStockQuantity()).isEqualTo(stockBefore + 2);
        verify(productRepository).save(product);
    }
}
