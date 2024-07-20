package com.g1.kumaribookshopbackend.dto;

import com.g1.kumaribookshopbackend.entity.OrderDetail;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class OrderDetailDto extends SuperDto<OrderDetail> {
    private Long detailId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal sellingPrice;
    private BigDecimal totalPrice;

    @Override
    public OrderDetail toEntity() {
        return null;
    }
}
