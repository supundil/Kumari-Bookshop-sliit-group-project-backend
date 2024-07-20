package com.g1.kumaribookshopbackend.dto;

import com.g1.kumaribookshopbackend.enums.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CustomerOrderWrapperDto {
    private String username;
    private Long oderId;
    private OrderStatus orderStatus;
    private Integer productCount;
    private BigDecimal totalCost = BigDecimal.valueOf(12500);
    private List<OrderDetailDto> orderDetailDtoList;
}
