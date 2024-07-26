package com.g1.kumaribookshopbackend.repository;

import com.g1.kumaribookshopbackend.entity.Customer;
import com.g1.kumaribookshopbackend.entity.CustomerOrder;
import com.g1.kumaribookshopbackend.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder,Long> {
    Optional<CustomerOrder> findFirstByOrderStatusAndCustomer(OrderStatus orderStatus, Customer customer);
    List<CustomerOrder> findAllByCustomer(Customer customer);
    List<CustomerOrder> findAllByOrderStatus(OrderStatus orderStatus);
}
