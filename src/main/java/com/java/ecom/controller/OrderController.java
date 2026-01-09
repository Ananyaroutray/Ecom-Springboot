package com.java.ecom.controller;

import com.java.ecom.dto.request.CheckoutRequestDto;
import com.java.ecom.dto.response.OrderResponseDto;
import com.java.ecom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // CHECKOUT--PLACE ORDER
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<OrderResponseDto> checkout(
            @PathVariable UUID userId,
            @RequestBody CheckoutRequestDto dto) {

        OrderResponseDto response = orderService.checkout(userId, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
