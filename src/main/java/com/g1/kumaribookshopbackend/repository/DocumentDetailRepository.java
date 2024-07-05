package com.g1.kumaribookshopbackend.repository;

import com.g1.kumaribookshopbackend.entity.DocumentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentDetailRepository extends JpaRepository<DocumentDetail,Long> {
}
