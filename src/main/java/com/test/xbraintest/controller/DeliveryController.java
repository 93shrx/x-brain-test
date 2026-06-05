package com.test.xbraintest.controller;

import com.test.xbraintest.domain.Delivery;
import com.test.xbraintest.repository.DeliveryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deliveries")
@Tag(name = "Deliveries", description = "Endpoints for retrieving processed deliveries")
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;

    @GetMapping
    @Operation(summary = "List all deliveries", description = "Retrieves all processed deliveries from asynchronous order processing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deliveries retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Delivery> findAll() {
        return deliveryRepository.findAll();
    }
}