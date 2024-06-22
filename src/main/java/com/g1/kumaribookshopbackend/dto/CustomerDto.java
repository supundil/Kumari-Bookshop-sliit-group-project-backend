package com.g1.kumaribookshopbackend.dto;

import com.g1.kumaribookshopbackend.entity.Customer;
import lombok.Data;



@Data
public class CustomerDto extends SuperDto<Customer> {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String address;
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
        customer.setFirstName(this.firstName);
        customer.setLastName(this.lastName);
        customer.setAddress(this.address);
        customer.setEmailAddress(this.emailAddress);
        customer.setContactNo(this.contactNo);
        customer.setUserName(this.userName);
        customer.setPassword(this.password);
        return customer;
    }
}
