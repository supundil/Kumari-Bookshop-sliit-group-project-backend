package com.g1.kumaribookshopbackend.service;

import com.g1.kumaribookshopbackend.util.StandardResponse;

public interface LoginService {

    StandardResponse customerLogin(String userName, String password);

    StandardResponse adminLogin(String userName, String password);

}
