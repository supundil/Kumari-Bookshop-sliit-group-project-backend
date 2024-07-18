package com.g1.kumaribookshopbackend.service;

import com.g1.kumaribookshopbackend.dto.ListItemDto;
import com.g1.kumaribookshopbackend.dto.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Boolean saveProduct(ProductDto productDto, MultipartFile file);

    Boolean updateProduct(ProductDto productDto, MultipartFile file);

    Boolean deleteProduct(Long productId);

    List<ProductDto> getAllProducts();

    ProductDto getProduct(Long productId);

    List<ProductDto> getAllActiveProducts();

    List<ListItemDto> getAllActiveProductCategories();
}
