package com.g1.kumaribookshopbackend.dto;

import com.g1.kumaribookshopbackend.entity.Customer;
import lombok.Data;



@Data
public class CustomerDto extends SuperDto<Customer> {
    private Long customerId;
    private String name;
    private String nic;
    private String emailAddress;
    private String contactNo;
    private String userName;
    private String password;

//    private Set<OrderDto> orderDtoList;
//
//    private Set<PaymentDto> paymentDtoList;

    @Override
    public Customer toEntity() {
        Customer customer = new Customer();
        customer.setCustomerId(this.customerId);
        customer.setName(this.name);
        customer.setNic(this.nic);
        customer.setEmailAddress(this.emailAddress);
        customer.setContactNo(this.contactNo);
        customer.setUserName(this.userName);
        return customer;
    }
}
