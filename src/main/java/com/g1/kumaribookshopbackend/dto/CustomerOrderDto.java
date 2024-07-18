package com.g1.kumaribookshopbackend.dto;

import com.g1.kumaribookshopbackend.entity.SuperEntity;
import com.g1.kumaribookshopbackend.enums.OrderStatus;
import lombok.Data;

@Data
public class CustomerOrderDto extends SuperDto {
    private Long oderId;
    private OrderStatus orderStatus = OrderStatus.PENDING;
    private String username;
    private Long productId;
    private Integer quantity;

    @Override
    public SuperEntity toEntity() {
        return null;
    }
}
