package com.g1.kumaribookshopbackend.repository;

import com.g1.kumaribookshopbackend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {

    Boolean existsByUserName(String username);

    Optional<Admin> findByUserName(String userName);
}
