package com.g1.kumaribookshopbackend.dto;

import com.g1.kumaribookshopbackend.enums.OrderStatus;
import lombok.Data;

import java.util.List;

@Data
public class CustomerOrderWrapperDto {
    private String username;
    private Long oderId;
    private OrderStatus orderStatus;
    private Integer productCount;
    private List<OrderDetailDto> orderDetailDtoList;
}
