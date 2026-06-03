package com.test.xbraintest.messaging;

import com.test.xbraintest.config.RabbitMQConfig;
import com.test.xbraintest.dto.OrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(OrderMessage message) {
        log.info("Publishing order {} to queue", message.getOrderId());
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, message);
            log.info("Order {} published successfully", message.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish order {}", message.getOrderId(), e);
            throw e;
        }
    }
}
