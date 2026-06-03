package com.test.xbraintest.service;

import com.test.xbraintest.domain.Order;
import com.test.xbraintest.domain.OrderStatus;
import com.test.xbraintest.domain.Product;
import com.test.xbraintest.dto.OrderRequest;
import com.test.xbraintest.dto.OrderResponse;
import com.test.xbraintest.exception.OrderNotFoundException;
import com.test.xbraintest.messaging.OrderPublisher;
import com.test.xbraintest.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderPublisher orderPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldPersistAndPublish() {
        OrderRequest request = new OrderRequest();
        request.setCustomerCode("CUST-001");
        request.setProductCodes(List.of("PROD-A", "PROD-B"));
        request.setTotalAmount(new BigDecimal("150.00"));
        request.setDeliveryAddress("Rua das Flores, 123");

        Order savedOrder = Order.builder()
                .id(1L)
                .customerCode("CUST-001")
                .products(List.of(
                        Product.builder().productCode("PROD-A").build(),
                        Product.builder().productCode("PROD-B").build()))
                .totalAmount(new BigDecimal("150.00"))
                .deliveryAddress("Rua das Flores, 123")
                .status(OrderStatus.PUBLISHED)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerCode()).isEqualTo("CUST-001");
        assertThat(response.getProductCodes()).containsExactly("PROD-A", "PROD-B");
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PUBLISHED);

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderPublisher).publish(any());
    }

    @Test
    void getOrder_shouldReturnOrder_whenFound() {
        Order order = Order.builder()
                .id(1L)
                .customerCode("CUST-001")
                .products(List.of())
                .totalAmount(BigDecimal.TEN)
                .deliveryAddress("Rua A")
                .status(OrderStatus.CREATED)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrder(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void getOrder_shouldThrow_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(99L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("99");
    }
}
