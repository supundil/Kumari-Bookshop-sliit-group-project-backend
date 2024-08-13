package com.g1.kumaribookshopbackend.service.impl;

import com.g1.kumaribookshopbackend.dto.DocumentDetailDto;
import com.g1.kumaribookshopbackend.dto.ListItemDto;
import com.g1.kumaribookshopbackend.dto.ProductDto;
import com.g1.kumaribookshopbackend.entity.DocumentDetail;
import com.g1.kumaribookshopbackend.entity.Product;
import com.g1.kumaribookshopbackend.entity.ProductCategory;
import com.g1.kumaribookshopbackend.enums.RecordStatus;
import com.g1.kumaribookshopbackend.exception.BadRequestException;
import com.g1.kumaribookshopbackend.exception.InternalServerException;
import com.g1.kumaribookshopbackend.exception.NotFoundException;
import com.g1.kumaribookshopbackend.repository.ProductCategoryRepository;
import com.g1.kumaribookshopbackend.repository.ProductRepository;
import com.g1.kumaribookshopbackend.service.DocumentService;
import com.g1.kumaribookshopbackend.service.ProductService;
import com.g1.kumaribookshopbackend.util.MessageConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final DocumentService documentService;
    private final ProductCategoryRepository productCategoryRepository;


    @Transactional
    @Override
    public Boolean saveProduct(ProductDto productDto, MultipartFile file) {
        try {

            if (Objects.nonNull(productDto) && Objects.nonNull(productDto.getName()) && Objects.nonNull(productDto.getCategoryId()) && Objects.nonNull(file)) {

                DocumentDetailDto documentDetailDto = documentService.uploadImage(file);
                DocumentDetail documentDetail = documentDetailDto.toEntity();

                Product product = productDto.toEntity();
                product.setDocumentDetail(documentDetail);



                productRepository.save(product);
                return true;

            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }

        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
           log.error("saveProduct failed : " + e.getMessage());
           throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean updateProduct(ProductDto productDto, MultipartFile file) {
        try {

            if (Objects.nonNull(productDto) && Objects.nonNull(productDto.getName()) && Objects.nonNull(productDto.getProductId()) && Objects.nonNull(productDto.getCategoryId())) {

                Optional<Product> product = productRepository.findById(productDto.getProductId());

                if (product.isPresent()) {

                    DocumentDetail documentDetail = product.get().getDocumentDetail();
                    if (Objects.nonNull(file)) {
                        DocumentDetailDto documentDetailDto = documentService.updateImage(file, product.get().getDocumentDetail().getDocumentId());
                        documentDetail = documentDetailDto.toEntity();
                    }

                    Product save = productDto.toEntity();
                    save.setDocumentDetail(documentDetail);
                    productRepository.save(save);

                    return true;

                } else {
                    throw new NotFoundException(MessageConstant.PRODUCT_NOT_FOUND);
                }

            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }

        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error("updateProduct failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean deleteProduct(Long productId) {
        try {

            if (Objects.nonNull(productId)) {

                Optional<Product> product = productRepository.findById(productId);

                if (product.isPresent()) {
                    productRepository.deleteById(productId);
                    return documentService.deleteImage(product.get().getDocumentDetail().getFileId());
                } else {
                    return true;

                }

            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }

        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
           log.error("deleteProduct failed : " + e.getMessage());
           throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<ProductDto> getAllProducts() {
        try {

            return productRepository.findAll().stream().map(this::productCardDetails).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("getAllProducts : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ProductDto getProduct(Long productId) {
        try {

            if (Objects.nonNull(productId)) {

                Optional<Product> product = productRepository.findById(productId);
                if (product.isPresent()) {
                    ProductDto productDto = product.get().toDto();
                    productDto.setCategoryId(product.get().getProductCategory().getCategoryId());
                    productDto.setCategoryName(product.get().getProductCategory().getName());
                    productDto.setProductImageName(product.get().getDocumentDetail().getFileName());
                    productDto.setImageBase64(product.get().getDocumentDetail().getImage());
                    return productDto;
                } else {
                    throw new NotFoundException(MessageConstant.PRODUCT_NOT_FOUND);
                }

            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }

        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("getProduct : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<ProductDto> getAllActiveProducts() {
        try {

            return productRepository.findAllByRecordStatus(RecordStatus.ACTIVE).stream().map(this::productCardDetails).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("getAllProducts : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<ListItemDto> getAllActiveProductCategories() {
        try {
            List<ListItemDto> listItemDtos = new ArrayList<>();
            List<ProductCategory> categories = productCategoryRepository.findAllByRecordStatus(RecordStatus.ACTIVE);
            if (!CollectionUtils.isEmpty(categories)) {
                listItemDtos.addAll(categories.stream().map(productCategory -> {
                    return ListItemDto.builder().value(productCategory.getCategoryId()).label(productCategory.getName()).build();
                }).toList());
                return listItemDtos;
            }
            return listItemDtos;

        } catch (Exception e) {
            log.error("getAllActiveProductCategories : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }


    private ProductDto productCardDetails(Product product) {
        ProductDto productDto = product.toDto();
        productDto.setProductImageName(product.getDocumentDetail().getFileName());
        productDto.setImageBase64(product.getDocumentDetail().getImage());
        return productDto;
    }
}
