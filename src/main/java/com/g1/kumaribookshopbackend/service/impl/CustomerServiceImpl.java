package com.g1.kumaribookshopbackend.service.impl;

import com.g1.kumaribookshopbackend.dto.CustomerDto;
import com.g1.kumaribookshopbackend.dto.RequestDto;
import com.g1.kumaribookshopbackend.entity.Customer;
import com.g1.kumaribookshopbackend.exception.AlreadyExistException;
import com.g1.kumaribookshopbackend.exception.BadRequestException;
import com.g1.kumaribookshopbackend.exception.InternalServerException;
import com.g1.kumaribookshopbackend.exception.NotFoundException;
import com.g1.kumaribookshopbackend.repository.CustomerRepository;
import com.g1.kumaribookshopbackend.service.CustomerService;
import com.g1.kumaribookshopbackend.util.MessageConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class CustomerServiceImpl extends UtilService implements CustomerService {

    private CustomerRepository customerRepository;

    @Override
    public CustomerDto getCustomerByUserName(String username) {
        try {
            if (Objects.nonNull(username)) {
                Customer customer = customerRepository.findByUserName(username).orElseThrow(() -> {
                    throw new NotFoundException(MessageConstant.USER_NOT_FOUND);
                });
                return customer.toDto();
            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("Get customer by username fetch failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
    try {
        return customerRepository.findAll().stream().map(Customer::toDto).collect(Collectors.toList());
    } catch (Exception e) {
        log.error("Get all customers fetch failed : " + e.getMessage());
        throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
    }
    }

    @Override
    public RequestDto saveCustomer(CustomerDto customerDto) {
        try {

            if (Objects.nonNull(customerDto)) {
                RequestDto requestDto = new RequestDto();

                Boolean isExist = customerRepository.existsByUserName(customerDto.getUserName());
                if (isExist){
                    throw new AlreadyExistException(MessageConstant.USERNAME_ALREADY_EXIST);
                }
                Customer customer = customerDto.toEntity();
                customer.setPassword(hidePassword(customerDto.getPassword()));
                customerRepository.save(customer);

                requestDto.setIsAdmin(false);
                requestDto.setUsername(customerDto.getUserName());
                requestDto.setToken(getUserTaken(customerDto.getUserName(),customerDto.getPassword()));
                return requestDto;

            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }

        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (AlreadyExistException e) {
            throw new AlreadyExistException(e.getMessage());
        } catch (Exception e) {
            log.error("Save customer failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<CustomerDto> searchCustomersByUserName(String userName) {
        try {
            List<Customer> customers = customerRepository.searchCustomerByUserName(userName);
            return customers.stream().map(Customer::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Search Customer By User Name Failed --> " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }


}
