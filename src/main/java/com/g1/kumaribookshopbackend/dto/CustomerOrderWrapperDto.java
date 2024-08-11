package com.g1.kumaribookshopbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.g1.kumaribookshopbackend.enums.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerOrderWrapperDto {
    private String username;
    private Long oderId;
    private OrderStatus orderStatus;
    private Integer productCount;
    private BigDecimal totalCost;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    private List<OrderDetailDto> orderDetailDtoList;
}
