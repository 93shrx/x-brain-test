package com.test.xbraintest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage {
    private Long orderId;
    private String customerCode;
    private List<String> productCodes;
    private BigDecimal totalAmount;
    private String deliveryAddress;
}
