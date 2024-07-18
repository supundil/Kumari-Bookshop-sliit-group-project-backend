package com.g1.kumaribookshopbackend.entity;

import com.g1.kumaribookshopbackend.dto.ProductDto;
import com.g1.kumaribookshopbackend.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product extends SuperEntity<ProductDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @Column
    private String code;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private BigDecimal buyingPrice;
    @Column
    private BigDecimal sellingPrice;
    @Column
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private RecordStatus recordStatus = RecordStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id")
    private ProductCategory productCategory;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document_detail_id")
    private DocumentDetail documentDetail;

    @OneToMany(targetEntity = OrderDetail.class, mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderDetail> orderDetailSet;

    @Override
    public ProductDto toDto() {
        ProductDto productDto = new ProductDto();
        productDto.setCreatedDate(this.getCreatedDate());
        productDto.setModifiedDate(this.getModifiedDate());
        productDto.setProductId(this.productId);
        productDto.setCode(this.code);
        productDto.setName(this.name);
        productDto.setDescription(this.description);
        productDto.setBuyingPrice(this.buyingPrice);
        productDto.setSellingPrice(this.sellingPrice);
        productDto.setQuantity(this.quantity);
        return productDto;
    }

}
