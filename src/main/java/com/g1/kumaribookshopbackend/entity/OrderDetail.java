package com.g1.kumaribookshopbackend.entity;

import com.g1.kumaribookshopbackend.dto.OrderDetailDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrderDetail extends SuperEntity<OrderDetailDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oderDetailId;
    @Column
    private Integer productQnt;
    @Column
    private BigDecimal productTotalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private CustomerOrder customerOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Override
    public OrderDetailDto toDto() {
        OrderDetailDto orderDetailDto = new OrderDetailDto();
        orderDetailDto.setDetailId(this.oderDetailId);
        orderDetailDto.setProductId(this.product.getProductId());
        orderDetailDto.setProductName(this.product.getName());
        orderDetailDto.setQuantity(this.productQnt);
        orderDetailDto.setSellingPrice(this.product.getSellingPrice());
        orderDetailDto.setTotalPrice(this.productTotalPrice);
        orderDetailDto.setImage(this.product.getDocumentDetail().getImage());
        orderDetailDto.setCreatedDate(this.getCreatedDate());
        return orderDetailDto;
    }
}
