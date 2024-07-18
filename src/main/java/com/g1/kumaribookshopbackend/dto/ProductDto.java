package com.g1.kumaribookshopbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.g1.kumaribookshopbackend.entity.Product;
import com.g1.kumaribookshopbackend.entity.ProductCategory;
import lombok.Data;

import java.math.BigDecimal;
@Data
@JsonFormat
public class ProductDto extends SuperDto<Product> {
    private Long productId;
    private String code;
    private String name;
    private String description;
    private BigDecimal buyingPrice;
    private BigDecimal sellingPrice;
    private Integer quantity;
    private Long categoryId;
    private String categoryName;
    private String productImageName;
    private String imageBase64;

    @Override
    public Product toEntity() {
        Product product = new Product();
        product.setProductId(this.productId);
        product.setCode(this.code);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setBuyingPrice(this.buyingPrice);
        product.setSellingPrice(this.sellingPrice);
        product.setQuantity(this.quantity);
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryId(this.categoryId);
        product.setProductCategory(productCategory);
        return product;
    }
}
