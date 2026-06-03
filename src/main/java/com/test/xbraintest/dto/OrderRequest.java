package com.test.xbraintest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {

    @NotBlank
    private String customerCode;

    @NotEmpty
    private List<String> productCodes;

    @NotNull
    @Positive
    private BigDecimal totalAmount;

    @NotBlank
    private String deliveryAddress;
}
