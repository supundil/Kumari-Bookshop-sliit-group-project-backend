package com.g1.kumaribookshopbackend.service;

import com.g1.kumaribookshopbackend.dto.CustomerDto;
import com.g1.kumaribookshopbackend.dto.RequestDto;

import java.util.List;


public interface CustomerService {

    CustomerDto getCustomerByUserName(String username);

    List<CustomerDto> getAllCustomers();

    RequestDto saveCustomer(CustomerDto customerDto);

    List<CustomerDto> searchCustomersByUserName(String userName);

}
