package com.g1.kumaribookshopbackend.service;

import com.g1.kumaribookshopbackend.dto.CustomerDto;
import com.g1.kumaribookshopbackend.util.StandardResponse;

import java.util.List;


public interface CustomerService {

    StandardResponse getCustomerByUserName(String username);

    StandardResponse getAllCustomers();

    StandardResponse saveCustomer(CustomerDto customerDto);

    List<CustomerDto> searchCustomersByUserName(String userName);

}
