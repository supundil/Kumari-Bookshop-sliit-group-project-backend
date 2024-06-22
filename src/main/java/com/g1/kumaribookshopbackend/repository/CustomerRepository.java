package com.g1.kumaribookshopbackend.repository;

import com.g1.kumaribookshopbackend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Boolean existsByUserName(String userName);

    Optional<Customer> findByUserName(String userName);

    @Query(value = "SELECT * FROM Customer WHERE customer.user_name LIKE '%na%'", nativeQuery = true)
    List<Customer> searchCustomerByUserName(String userName);
}
