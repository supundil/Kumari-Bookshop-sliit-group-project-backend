package com.g1.kumaribookshopbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.g1.kumaribookshopbackend.dto.ListItemDto;
import com.g1.kumaribookshopbackend.dto.ProductDto;
import com.g1.kumaribookshopbackend.service.ProductService;
import com.g1.kumaribookshopbackend.service.impl.UtilService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.g1.kumaribookshopbackend.util.AppConstant.ADMIN_ROLE;
import static com.g1.kumaribookshopbackend.util.AppConstant.USER_ROLE;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper mapper;
    private final UtilService utilService;


    @PostMapping("/save")
    public ResponseEntity<Boolean> saveProduct(@RequestHeader("Authorization") String token,@RequestParam("file") MultipartFile file, @RequestParam("productDto") String productDtoData) throws JsonProcessingException {
        if (utilService.requestAuthentication(token,ADMIN_ROLE)) {
            ProductDto productDto = mapper.readValue(productDtoData, new TypeReference<ProductDto>() {});
            return new ResponseEntity<>(productService.saveProduct(productDto,file), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean> updateProduct(@RequestHeader("Authorization") String token,@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("productDto") String productDtoData) throws JsonProcessingException {
        if (utilService.requestAuthentication(token,ADMIN_ROLE)) {
            ProductDto productDto = mapper.readValue(productDtoData, new TypeReference<ProductDto>() {});
            return new ResponseEntity<>(productService.updateProduct(productDto,file), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@RequestHeader("Authorization") String token,@PathVariable Long productId) {
        if (utilService.requestAuthentication(token,ADMIN_ROLE)) {
            return new ResponseEntity<>(productService.deleteProduct(productId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/get/{productId}")
    public ResponseEntity<ProductDto> getProduct(@RequestHeader("Authorization") String token,@PathVariable Long productId) {
        if (utilService.requestAuthentication(token,ADMIN_ROLE)){
            return new ResponseEntity<>(productService.getProduct(productId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ProductDto>> getAllProducts(@RequestHeader("Authorization") String token) {
        if (utilService.requestAuthentication(token,ADMIN_ROLE)){
            return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/get-all-active")
    public ResponseEntity<List<ProductDto>> getAllActiveProducts(@RequestHeader("Authorization") String token) {
        if (utilService.requestAuthentication(token,USER_ROLE)){
            return new ResponseEntity<>(productService.getAllActiveProducts(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/get-all-active-categories")
    public ResponseEntity<List<ListItemDto>> getAllActiveProductCategories(@RequestHeader("Authorization") String token) {
        if (utilService.requestAuthentication(token,ADMIN_ROLE)){
            return new ResponseEntity<>(productService.getAllActiveProductCategories(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/get-detail/{productId}")
    public ResponseEntity<ProductDto> getProductDetail(@RequestHeader("Authorization") String token,@PathVariable Long productId) {
        if (utilService.requestAuthentication(token,USER_ROLE)){
            return new ResponseEntity<>(productService.getProduct(productId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
