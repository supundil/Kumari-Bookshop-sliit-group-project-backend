package com.g1.kumaribookshopbackend.controller;

import com.g1.kumaribookshopbackend.dto.CustomerOrderDto;
import com.g1.kumaribookshopbackend.dto.RequestDto;
import com.g1.kumaribookshopbackend.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/order-service")
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/add-to-cart")
    public ResponseEntity<Boolean> addToCart(@RequestBody CustomerOrderDto customerOrderDto) {
        return new ResponseEntity<>(orderService.addToCart(customerOrderDto), HttpStatus.OK);
    }

}
