package com.g1.kumaribookshopbackend.service;

import com.g1.kumaribookshopbackend.dto.CustomerOrderDto;
import com.g1.kumaribookshopbackend.dto.CustomerOrderWrapperDto;

import java.util.List;

public interface OrderService {
    Boolean addToCart(CustomerOrderDto customerOrderDto);
    CustomerOrderWrapperDto getCart(String username);
    Boolean increaseProductQuantity(Long detailId);
    Boolean decreaseProductQuantity(Long detailId);
    Boolean placeOrder(String username);
    List<CustomerOrderWrapperDto> getAllOrders(String username);
    List<CustomerOrderWrapperDto> getAllCustomerSubmittedOrders();
    List<CustomerOrderWrapperDto> getAllConfirmedOrders();
    List<CustomerOrderWrapperDto> getAllPaidOrders();
    Boolean confirmCustomerOrder(Long orderId);
    Boolean rejectCustomerOrder(Long orderId);
    Boolean closeCustomerOrder(Long orderId);
}
