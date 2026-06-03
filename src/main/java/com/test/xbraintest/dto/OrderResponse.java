package com.test.xbraintest.dto;

import com.test.xbraintest.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String customerCode;
    private List<String> productCodes;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
