package com.g1.kumaribookshopbackend.service.impl;

import com.g1.kumaribookshopbackend.dto.DocumentDetailDto;
import com.g1.kumaribookshopbackend.entity.DocumentDetail;
import com.g1.kumaribookshopbackend.exception.BadRequestException;
import com.g1.kumaribookshopbackend.exception.InternalServerException;
import com.g1.kumaribookshopbackend.exception.NotFoundException;
import com.g1.kumaribookshopbackend.repository.DocumentDetailRepository;
import com.g1.kumaribookshopbackend.service.DocumentService;
import com.g1.kumaribookshopbackend.util.MessageConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentDetailRepository documentDetailRepository;
    private final FirebaseServiceImpl firebaseService;


    @Override
    public DocumentDetailDto uploadImage(MultipartFile file) {
        try {

            return firebaseService.upload(file);

        } catch (Exception e) {
            log.error("uploadImage failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DocumentDetailDto updateImage(MultipartFile file, Long docId) {
        try {

            DocumentDetail documentDetail = documentDetailRepository.findById(docId).orElse(null);

            if (Objects.nonNull(documentDetail)) {
                DocumentDetailDto updateFile = firebaseService.update(file,documentDetail.getFileId());

                documentDetail.setFileId(updateFile.getFileId());
                documentDetail.setFileName(updateFile.getFileName());
                documentDetailRepository.save(documentDetail);
                return documentDetail.toDto();
            } else {
                throw new NotFoundException(MessageConstant.DOCUMENT_DETAIL_NOT_FOUND);
            }

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("updatedImage failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getImage(String fileId) {
        try {

            if (Objects.nonNull(fileId)) {

//                return firebaseService.download(fileId);
                return null;
            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }

        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error("getImage failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean deleteImage(String fileId) {
        try {

            if (Objects.nonNull(fileId)) {

                return firebaseService.delete(fileId);

            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }

        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error("deleteImage failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }
}
