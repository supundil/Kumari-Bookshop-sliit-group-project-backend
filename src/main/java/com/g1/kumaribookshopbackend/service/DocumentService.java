package com.g1.kumaribookshopbackend.service;

import com.g1.kumaribookshopbackend.dto.DocumentDetailDto;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    DocumentDetailDto uploadImage(MultipartFile file);
    DocumentDetailDto updateImage(MultipartFile file, Long docId);
    String getImage(String fileId);
    Boolean deleteImage(String fileId);
}
