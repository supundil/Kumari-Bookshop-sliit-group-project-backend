package com.g1.kumaribookshopbackend.service.impl;


import com.g1.kumaribookshopbackend.entity.Admin;
import com.g1.kumaribookshopbackend.entity.Customer;
import com.g1.kumaribookshopbackend.exception.NotFoundException;
import com.g1.kumaribookshopbackend.exception.ValidateException;
import com.g1.kumaribookshopbackend.repository.AdminRepository;
import com.g1.kumaribookshopbackend.repository.CustomerRepository;
import com.g1.kumaribookshopbackend.service.LoginService;
import com.g1.kumaribookshopbackend.util.StandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginServiceImpl extends UtilService implements LoginService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CustomerRepository customerRepository;


    @Override
    public StandardResponse customerLogin(String userName, String password) {
        try {
            Customer customer = customerRepository.findByUserName(userName).orElseThrow(()->{
                throw new NotFoundException("Username not found : " + userName);
            });

            Boolean isPasswordAuthenticated = checkPassword(customer.getPassword(),password);
            if (!isPasswordAuthenticated){
                throw new ValidateException("Invalid password.");
            }
            String userTaken = getUserTaken(userName,password);
            return new StandardResponse("200", "Successful", userTaken, null);
        } catch (Exception e) {
            log.error("Customer login failed : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public StandardResponse adminLogin(String userName, String password) {
        try {
            Admin admin = adminRepository.findByUserName(userName).orElseThrow(()->{
                throw new NotFoundException("Username not found : " + userName);
            });

            Boolean isPasswordAuthenticated = checkPassword(admin.getPassword(),password);
            if (!isPasswordAuthenticated){
                throw new ValidateException("Invalid password.");
            }
            String userToken = getUserTaken(userName,password);
            return new StandardResponse("200", "Successful", userToken, null);
        } catch (Exception e) {
            log.error("Admin login failed : " + e.getMessage());
            throw e;
        }
    }
}
