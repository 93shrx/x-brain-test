package com.test.xbraintest.messaging;

import com.test.xbraintest.domain.Delivery;
import com.test.xbraintest.dto.OrderMessage;
import com.test.xbraintest.repository.DeliveryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryConsumerTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryConsumer deliveryConsumer;

    @Test
    void consume_shouldPersistDelivery() {
        OrderMessage message = OrderMessage.builder()
                .orderId(1L)
                .customerCode("CUST-001")
                .productCodes(List.of("PROD-A"))
                .totalAmount(new BigDecimal("100.00"))
                .deliveryAddress("Rua das Flores, 123")
                .build();

        deliveryConsumer.consume(message);

        ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository).save(captor.capture());

        Delivery saved = captor.getValue();
        assertThat(saved.getOrderId()).isEqualTo(1L);
        assertThat(saved.getDeliveryAddress()).isEqualTo("Rua das Flores, 123");
    }
}
