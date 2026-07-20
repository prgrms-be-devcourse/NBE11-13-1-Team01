package com.composebean.controller;

import com.example.spring.basicboard.temp.dto.OrderDto;
import com.example.spring.basicboard.temp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(
            @RequestParam(value = "email", required = false) String email) {

        if (email != null) {
            // email이 있을 때: 특정 유저 주문 조회
            return ResponseEntity.ok(orderService.findByEmail(email));
        } else {
            // email이 없을 때: 전체 주문 조회
            return ResponseEntity.ok(orderService.findAll());
        }
    }
}
