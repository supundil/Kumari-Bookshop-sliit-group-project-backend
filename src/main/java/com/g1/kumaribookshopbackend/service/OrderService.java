package com.g1.kumaribookshopbackend.service;

import com.g1.kumaribookshopbackend.dto.CustomerOrderDto;
import com.g1.kumaribookshopbackend.dto.CustomerOrderWrapperDto;

public interface OrderService {
    Boolean addToCart(CustomerOrderDto customerOrderDto);
    CustomerOrderWrapperDto getCart(String username);
    Boolean increaseProductQuantity(Long detailId);
    Boolean decreaseProductQuantity(Long detailId);
    Boolean placeOrder(String username);
}
