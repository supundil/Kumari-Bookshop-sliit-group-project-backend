package com.g1.kumaribookshopbackend.service.impl;


import com.g1.kumaribookshopbackend.dto.RequestDto;
import com.g1.kumaribookshopbackend.entity.Admin;
import com.g1.kumaribookshopbackend.entity.Customer;
import com.g1.kumaribookshopbackend.exception.BadRequestException;
import com.g1.kumaribookshopbackend.exception.InternalServerException;
import com.g1.kumaribookshopbackend.exception.NotFoundException;
import com.g1.kumaribookshopbackend.exception.ValidateException;
import com.g1.kumaribookshopbackend.repository.AdminRepository;
import com.g1.kumaribookshopbackend.repository.CustomerRepository;
import com.g1.kumaribookshopbackend.service.LoginService;
import com.g1.kumaribookshopbackend.util.MessageConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class LoginServiceImpl extends UtilService implements LoginService {

    private AdminRepository adminRepository;
    private CustomerRepository customerRepository;


    @Override
    public RequestDto userLogin(RequestDto requestDto) {
        try {
            if (Objects.nonNull(requestDto)) {

                boolean isPasswordAuthenticated = false;

                Admin admin = adminRepository.findByUserName(requestDto.getUsername()).orElse(null);

                if (Objects.nonNull(admin)) {
                    isPasswordAuthenticated = checkPassword(admin.getPassword(), requestDto.getPassword());
                    requestDto.setIsAdmin(true);
                }

                Customer customer = customerRepository.findByUserName(requestDto.getUsername()).orElse(null);
                if (Objects.nonNull(customer)) {
                    isPasswordAuthenticated = checkPassword(customer.getPassword(), requestDto.getPassword());
                    requestDto.setIsAdmin(false);
                }

                if (Objects.isNull(admin) && Objects.isNull(customer)) {
                    throw new NotFoundException(MessageConstant.USER_NOT_FOUND);
                }

                if (!isPasswordAuthenticated) {
                    throw new ValidateException("Invalid password.");
                }

                requestDto.setToken(getUserTaken(requestDto.getUsername(),requestDto.getPassword()));
                requestDto.setPassword(null);
                return requestDto;

            } else {
                throw new BadRequestException("User credential can not be empty");
            }
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("User login failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }
}
