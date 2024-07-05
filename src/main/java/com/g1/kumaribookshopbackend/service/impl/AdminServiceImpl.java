package com.g1.kumaribookshopbackend.service.impl;

import com.g1.kumaribookshopbackend.dto.AdminDto;
import com.g1.kumaribookshopbackend.dto.RequestDto;
import com.g1.kumaribookshopbackend.entity.Admin;
import com.g1.kumaribookshopbackend.exception.AlreadyExistException;
import com.g1.kumaribookshopbackend.exception.BadRequestException;
import com.g1.kumaribookshopbackend.exception.InternalServerException;
import com.g1.kumaribookshopbackend.repository.AdminRepository;
import com.g1.kumaribookshopbackend.service.AdminService;
import com.g1.kumaribookshopbackend.util.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminServiceImpl extends UtilService implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public RequestDto saveAdmin(AdminDto adminDto) {
        try {
            RequestDto requestDto = new RequestDto();

            if (Objects.nonNull(adminDto)) {

                Boolean isExist = adminRepository.existsByUserName(adminDto.getUserName());
                if (isExist){
                    throw new AlreadyExistException(MessageConstant.USERNAME_ALREADY_EXIST);
                }

                Admin admin = adminDto.toEntity();
                admin.setPassword(hidePassword(admin.getPassword()));
                adminRepository.save(admin);

                requestDto.setIsAdmin(true);
                requestDto.setUsername(admin.getUserName());
                requestDto.setToken(getUserTaken(adminDto.getUserName(),adminDto.getPassword()));

            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }
            return requestDto;

        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (AlreadyExistException e) {
            throw new AlreadyExistException(e.getMessage());
        } catch (Exception e) {
            log.error("Save admin failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR) ;
        }
    }

    @Override
    public List<AdminDto> getAllAdmins() {
        try {
            return adminRepository.findAll().stream().map(Admin::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Get all admins fetch failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }
}
