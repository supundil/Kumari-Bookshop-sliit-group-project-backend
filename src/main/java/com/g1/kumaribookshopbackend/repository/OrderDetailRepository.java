package com.g1.kumaribookshopbackend.repository;


import com.g1.kumaribookshopbackend.entity.CustomerOrder;
import com.g1.kumaribookshopbackend.entity.OrderDetail;
import com.g1.kumaribookshopbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    List<OrderDetail> findAllByCustomerOrder(CustomerOrder customerOrder);
    Optional<OrderDetail> findByCustomerOrderAndProduct(CustomerOrder customerOrder, Product product);
}
