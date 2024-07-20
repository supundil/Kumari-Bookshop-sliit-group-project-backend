package com.g1.kumaribookshopbackend.controller;

import com.g1.kumaribookshopbackend.dto.CustomerOrderDto;
import com.g1.kumaribookshopbackend.dto.CustomerOrderWrapperDto;
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

    @GetMapping("/get-cart/{username}")
    public ResponseEntity<CustomerOrderWrapperDto> getCart(@PathVariable String username) {
        return new ResponseEntity<>(orderService.getCart(username), HttpStatus.OK);
    }

    @PostMapping("/increase-product-quantity/{detailId}")
    public ResponseEntity<Boolean> increaseProductQuantity(@PathVariable Long detailId) {
        return new ResponseEntity<>(orderService.increaseProductQuantity(detailId), HttpStatus.OK);
    }

    @PostMapping("/decrease-product-quantity/{detailId}")
    public ResponseEntity<Boolean> decreaseProductQuantity(@PathVariable Long detailId) {
        return new ResponseEntity<>(orderService.decreaseProductQuantity(detailId), HttpStatus.OK);
    }

    @PostMapping("/place-order/{username}")
    public ResponseEntity<Boolean> placeOrder(@PathVariable String username) {
        return new ResponseEntity<>(orderService.placeOrder(username), HttpStatus.OK);
    }

}
