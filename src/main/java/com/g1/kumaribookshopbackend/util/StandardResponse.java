package com.g1.kumaribookshopbackend.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StandardResponse {

    private String code;
    private String message;
    private Object data;
    private String token;
    private Object error;

    public StandardResponse(String code, String message, Object data, Object error){
        this.code = code;
        this.message = message;
        this.data = data;
        this.error = error;
    }

}
