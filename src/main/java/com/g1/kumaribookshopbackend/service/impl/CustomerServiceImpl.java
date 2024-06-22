package com.g1.kumaribookshopbackend.service.impl;

import com.g1.kumaribookshopbackend.dto.CustomerDto;
import com.g1.kumaribookshopbackend.entity.Customer;
import com.g1.kumaribookshopbackend.exception.AlreadyExistException;
import com.g1.kumaribookshopbackend.exception.NotFoundException;
import com.g1.kumaribookshopbackend.repository.CustomerRepository;
import com.g1.kumaribookshopbackend.service.CustomerService;
import com.g1.kumaribookshopbackend.util.StandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Thathsara Dananjaya <thathsaradananjaya@gmail.com>
 * @since 1/16/2023
 **/

@Service
@Transactional
@Slf4j
public class CustomerServiceImpl extends UtilService implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public StandardResponse getCustomerByUserName(String username) {
        try {
            Customer customer = customerRepository.findByUserName(username).orElseThrow(() -> {
                throw new NotFoundException("Username not found : " + username);
            });
            return new StandardResponse("200", "Successful", customer.toDto(), null);
        } catch (Exception e) {
            log.error("Get customer by username fetch failed : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public StandardResponse getAllCustomers() {
    try {
        List<CustomerDto> allCustomers = customerRepository.findAll().stream().map(Customer::toDto).collect(Collectors.toList());
        return new StandardResponse("200", "Successful", allCustomers, null);
    } catch (Exception e) {
        log.error("Get all customers fetch failed : " + e.getMessage());
        throw e;
    }
    }

    @Override
    public StandardResponse saveCustomer(CustomerDto customerDto) {
        try {
            Boolean isExist = customerRepository.existsByUserName(customerDto.getUserName());
            if (isExist){
                throw new AlreadyExistException("User name already exist.");
            }
            Customer customer = customerDto.toEntity();
            customer.setPassword(hidePassword(customerDto.getPassword()));
            customerRepository.save(customer);
            String userToken = getUserTaken(customerDto.getUserName(),customerDto.getPassword());
            return new StandardResponse("200", "Successful", userToken, null);
        }catch (Exception e) {
            log.error("Save customer failed : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<CustomerDto> searchCustomersByUserName(String userName) {
        try {
            List<Customer> customers = customerRepository.searchCustomerByUserName(userName);
            return customers.stream().map(Customer::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Search Customer By User Name Failed --> ");
            e.printStackTrace();
            throw e;
        }
    }


}
