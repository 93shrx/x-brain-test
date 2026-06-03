package com.test.xbraintest.service;

import com.test.xbraintest.domain.Order;
import com.test.xbraintest.domain.OrderStatus;
import com.test.xbraintest.domain.Product;
import com.test.xbraintest.dto.OrderMessage;
import com.test.xbraintest.dto.OrderRequest;
import com.test.xbraintest.dto.OrderResponse;
import com.test.xbraintest.exception.OrderNotFoundException;
import com.test.xbraintest.messaging.OrderPublisher;
import com.test.xbraintest.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderPublisher orderPublisher;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for customer {}", request.getCustomerCode());

        List<Product> products = request.getProductCodes().stream()
                .map(code -> Product.builder().productCode(code).build())
                .toList();

        Order order = Order.builder()
                .customerCode(request.getCustomerCode())
                .products(products)
                .totalAmount(request.getTotalAmount())
                .deliveryAddress(request.getDeliveryAddress())
                .status(OrderStatus.CREATED)
                .build();

        order = orderRepository.save(order);
        log.info("Order {} saved to database", order.getId());

        OrderMessage message = OrderMessage.builder()
                .orderId(order.getId())
                .customerCode(order.getCustomerCode())
                .productCodes(request.getProductCodes())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .build();

        orderPublisher.publish(message);

        order.setStatus(OrderStatus.PUBLISHED);
        order = orderRepository.save(order);
        log.info("Order {} status updated to PUBLISHED", order.getId());

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        return orderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private OrderResponse toResponse(Order order) {
        List<String> productCodes = order.getProducts().stream()
                .map(Product::getProductCode)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .customerCode(order.getCustomerCode())
                .productCodes(productCodes)
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
