package com.g1.kumaribookshopbackend.service.impl;

import com.g1.kumaribookshopbackend.dto.AdminDto;
import com.g1.kumaribookshopbackend.entity.Admin;
import com.g1.kumaribookshopbackend.exception.AlreadyExistException;
import com.g1.kumaribookshopbackend.repository.AdminRepository;
import com.g1.kumaribookshopbackend.service.AdminService;
import com.g1.kumaribookshopbackend.util.StandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AdminServiceImpl extends UtilService implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public StandardResponse saveAdmin(AdminDto adminDto) {
        try {
            Boolean isExist = adminRepository.existsByUserName(adminDto.getUserName());
            if (isExist){
                throw new AlreadyExistException("User name already exist.");
            }
            Admin admin = adminDto.toEntity();
            admin.setPassword(hidePassword(admin.getPassword()));
            adminRepository.save(admin);
            String userToken = getUserTaken(adminDto.getUserName(),adminDto.getPassword());
            return new StandardResponse("200", "Successful", userToken, null);
        } catch (Exception e) {
            log.error("Save admin failed : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public StandardResponse getAllAdmins() {
        try {
            List<AdminDto> allAdmins = adminRepository.findAll().stream().map(Admin::toDto).collect(Collectors.toList());
            return new StandardResponse("200", "Successful", allAdmins, null);
        } catch (Exception e) {
            log.error("Get all admins fetch failed : " + e.getMessage());
            throw e;
        }
    }
}
