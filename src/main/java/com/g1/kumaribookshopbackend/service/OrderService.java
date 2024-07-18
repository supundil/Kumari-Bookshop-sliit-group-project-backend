package com.g1.kumaribookshopbackend.service;

import com.g1.kumaribookshopbackend.dto.CustomerOrderDto;

public interface OrderService {
    Boolean addToCart(CustomerOrderDto customerOrderDto);
}
