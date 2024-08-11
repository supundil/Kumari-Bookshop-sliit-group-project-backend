package com.g1.kumaribookshopbackend.service;

import com.g1.kumaribookshopbackend.dto.AdminDto;
import com.g1.kumaribookshopbackend.dto.RequestDto;

import java.util.List;

public interface AdminService {

    RequestDto saveAdmin(AdminDto adminDto);

    Boolean updateAdminDetails(AdminDto adminDto);

    List<AdminDto> getAllAdmins();
}
