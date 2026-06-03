package com.test.xbraintest.controller;

import com.test.xbraintest.domain.Delivery;
import com.test.xbraintest.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;

    @GetMapping
    public List<Delivery> findAll() {
        return deliveryRepository.findAll();
    }
}