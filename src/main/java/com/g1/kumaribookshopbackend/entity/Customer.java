package com.g1.kumaribookshopbackend.entity;

import com.g1.kumaribookshopbackend.dto.CustomerDto;
import com.g1.kumaribookshopbackend.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Customer extends SuperEntity<CustomerDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    @Column
    private String name;
    @Column
    private String userName;
    @Column
    private String emailAddress;
    @Column
    private String contactNo;
    @Column
    private String nic;
    @Column
    private String password;
    @Enumerated(EnumType.STRING)
    private RecordStatus recordStatus = RecordStatus.ACTIVE;

//    @OneToMany(targetEntity = ProOrder.class, mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<ProOrder> proOrderList;
//
//    @OneToMany(targetEntity = Payment.class, mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<Payment> paymentList;

    @Override
    public CustomerDto toDto() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setCustomerId(this.customerId);
        customerDto.setName(this.name);
        customerDto.setNic(this.nic);
        customerDto.setEmailAddress(this.emailAddress);
        customerDto.setContactNo(this.contactNo);
        customerDto.setUserName(this.userName);
        return customerDto;
    }
}
