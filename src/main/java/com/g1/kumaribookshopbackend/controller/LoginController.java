package com.g1.kumaribookshopbackend.controller;

import com.g1.kumaribookshopbackend.dto.RequestDto;
import com.g1.kumaribookshopbackend.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/login")
public class LoginController {

    private LoginService loginService;

    @PostMapping("/user-login")
    public ResponseEntity<RequestDto> userLogin(@RequestBody RequestDto requestDto) {
        return new ResponseEntity<>(loginService.userLogin(requestDto), HttpStatus.OK);
    }

}
