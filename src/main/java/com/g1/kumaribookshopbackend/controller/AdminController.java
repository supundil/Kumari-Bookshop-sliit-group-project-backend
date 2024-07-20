package com.g1.kumaribookshopbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.g1.kumaribookshopbackend.dto.AdminDto;
import com.g1.kumaribookshopbackend.dto.RequestDto;
import com.g1.kumaribookshopbackend.service.AdminService;
import com.g1.kumaribookshopbackend.service.impl.UtilService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.g1.kumaribookshopbackend.util.AppConstant.ADMIN_ROLE;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/admin")
public class AdminController {

    private AdminService adminService;

    private UtilService utilService;

    @GetMapping("/getAll")
    public ResponseEntity<List<AdminDto>> getAllAdmins(@RequestHeader("Authorization") String token) {
        if (utilService.requestAuthentication(token,ADMIN_ROLE)){
            return new ResponseEntity<>(adminService.getAllAdmins(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/save")
    public ResponseEntity<RequestDto> saveAdmin(@RequestParam("adminDto") String adminDtoString) throws JsonProcessingException {
            AdminDto adminDto = new ObjectMapper().readValue(adminDtoString, AdminDto.class);
            return new ResponseEntity<>(adminService.saveAdmin(adminDto), HttpStatus.OK);
    }
}
