package com.g1.kumaribookshopbackend.service;

import com.g1.kumaribookshopbackend.dto.AdminDto;
import com.g1.kumaribookshopbackend.util.StandardResponse;

public interface AdminService {

    StandardResponse saveAdmin(AdminDto adminDto);

    StandardResponse getAllAdmins();
}
