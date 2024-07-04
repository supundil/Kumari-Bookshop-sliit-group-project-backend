package com.g1.kumaribookshopbackend.dto;

import lombok.Data;

@Data
public class RequestDto {
    private String username;
    private String password;
    private String token;
    private Boolean isAdmin;
}
