package com.g1.kumaribookshopbackend.controller;

import com.g1.kumaribookshopbackend.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/admin")
    public ResponseEntity adminLogin(@RequestParam String username, @RequestParam String password) {
        return new ResponseEntity<>(loginService.adminLogin(username,password), HttpStatus.OK);
    }

    @PostMapping("/customer")
    public ResponseEntity customerLogin(@RequestParam String username, @RequestParam String password) {
        return new ResponseEntity<>(loginService.customerLogin(username,password), HttpStatus.OK);
    }
}
