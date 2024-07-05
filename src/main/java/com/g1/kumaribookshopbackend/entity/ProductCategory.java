package com.g1.kumaribookshopbackend.entity;

import com.g1.kumaribookshopbackend.dto.ProductCategoryDto;
import com.g1.kumaribookshopbackend.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductCategory extends SuperEntity<ProductCategoryDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    @Column
    private String name;
    @Column
    private String code;
    @Column
    private String description;
    @Enumerated(EnumType.STRING)
    private RecordStatus recordStatus = RecordStatus.ACTIVE;

    @OneToMany(targetEntity = Product.class, mappedBy = "productCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Product> productList = new HashSet<>();

    @Override
    public ProductCategoryDto toDto() {
        ProductCategoryDto productCategoryDto = new ProductCategoryDto();
        productCategoryDto.setCategoryId(this.categoryId);
        productCategoryDto.setName(this.name);
        productCategoryDto.setCode(this.code);
        productCategoryDto.setDescription(this.description);
        return productCategoryDto;
    }
}
