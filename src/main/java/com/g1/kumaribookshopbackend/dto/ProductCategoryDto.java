package com.g1.kumaribookshopbackend.dto;

import com.g1.kumaribookshopbackend.entity.ProductCategory;
import lombok.Data;

@Data
public class ProductCategoryDto extends SuperDto<ProductCategory> {
    private Long categoryId;
    private String name;
    private String code;
    private String description;

    @Override
    public ProductCategory toEntity() {
        return null;
    }
}
