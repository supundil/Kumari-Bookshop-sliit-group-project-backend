package com.g1.kumaribookshopbackend.repository;

import com.g1.kumaribookshopbackend.entity.Product;
import com.g1.kumaribookshopbackend.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findAllByRecordStatus(RecordStatus recordStatus);
}
