package com.test.xbraintest.messaging;

import com.test.xbraintest.config.RabbitMQConfig;
import com.test.xbraintest.domain.Delivery;
import com.test.xbraintest.dto.OrderMessage;
import com.test.xbraintest.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryConsumer {

    private final DeliveryRepository deliveryRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consume(OrderMessage message) {
        log.info("Received order {} from queue, creating delivery", message.getOrderId());

        Delivery delivery = Delivery.builder()
                .orderId(message.getOrderId())
                .deliveryAddress(message.getDeliveryAddress())
                .build();

        deliveryRepository.save(delivery);
        log.info("Delivery created for order {}", message.getOrderId());
    }
}
