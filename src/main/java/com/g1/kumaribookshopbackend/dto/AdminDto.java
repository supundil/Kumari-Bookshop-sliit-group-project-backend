package com.g1.kumaribookshopbackend.dto;

import com.g1.kumaribookshopbackend.entity.Admin;
import lombok.Data;

@Data
public class AdminDto extends SuperDto<Admin> {
    private Long adminId;
    private String name;
    private String nic;
    private String address;
    private String userName;
    private String password;


    @Override
    public Admin toEntity() {
        Admin admin = new Admin();
        admin.setAdminId(this.adminId);
        admin.setName(this.name);
        admin.setAddress(this.address);
        admin.setUserName(this.userName);
        admin.setPassword(this.password);
        return admin;
    }
}
