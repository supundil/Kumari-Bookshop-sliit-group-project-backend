package com.g1.kumaribookshopbackend.controller;

import com.g1.kumaribookshopbackend.dto.CustomerDto;
import com.g1.kumaribookshopbackend.service.CustomerService;
import com.g1.kumaribookshopbackend.service.impl.UtilService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.g1.kumaribookshopbackend.util.AppConstant.ADMIN_ROLE;
import static com.g1.kumaribookshopbackend.util.AppConstant.USER_ROLE;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UtilService utilService;

    @GetMapping("/get-customer")
    public ResponseEntity geCustomerByUsername(@RequestHeader("Authorization") String token,@RequestParam String username) {
        if (utilService.requestAuthentication(token,USER_ROLE)){
            return new ResponseEntity<>(customerService.getCustomerByUserName(username), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/getAll")
    public ResponseEntity getAllCustomers(@RequestHeader("Authorization") String token) {
        if (utilService.requestAuthentication(token,ADMIN_ROLE)){
            return new ResponseEntity<>(customerService.getAllCustomers(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/save")
    public ResponseEntity saveCustomer(@RequestBody CustomerDto customerDto) {
        return new ResponseEntity<>(customerService.saveCustomer(customerDto), HttpStatus.OK);
    }

    @GetMapping("/search-by-userName")
    public ResponseEntity searchCustomersByUserName(@RequestParam String userName) {
        return new ResponseEntity<>(customerService.searchCustomersByUserName(userName), HttpStatus.OK);
    }
}
