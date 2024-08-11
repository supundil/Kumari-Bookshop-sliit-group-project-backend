package com.g1.kumaribookshopbackend.service.impl;

import com.g1.kumaribookshopbackend.dto.DocumentDetailDto;
import com.g1.kumaribookshopbackend.exception.InternalServerException;
import com.g1.kumaribookshopbackend.util.MessageConstant;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class FirebaseServiceImpl {

    private static final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/kumari-book-shop-inventory.appspot.com/o/%s?alt=media";

    private DocumentDetailDto uploadFile(MultipartFile file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("kumari-book-shop-inventory.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("kumari-book-shop-inventory-firebase-adminsdk-yod2u-e261c2a8ae.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, file.getBytes());
        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        DocumentDetailDto documentDetailDto = new DocumentDetailDto();
        documentDetailDto.setFileName(file.getOriginalFilename());
        documentDetailDto.setFileId(fileName);
        documentDetailDto.setFileBase64(String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8)));
        return documentDetailDto;
    }


    public Boolean deleteFile(String bucketName, String fileName) throws IOException {
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("kumari-book-shop-inventory-firebase-adminsdk-yod2u-e261c2a8ae.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        BlobId blobId = BlobId.of(bucketName, fileName);
        return storage.delete(blobId);
    }


    public DocumentDetailDto updateFile(String bucketName, String fileName, MultipartFile file) throws IOException {
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("kumari-book-shop-inventory-firebase-adminsdk-yod2u-e261c2a8ae.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);

        if (blob == null) {
            System.out.println("File not found");
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }

        byte[] fileBytes = file.getBytes();
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(blob.getContentType()).build();
        storage.create(blobInfo, fileBytes);

        System.out.printf((DOWNLOAD_URL) + "%n", URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        DocumentDetailDto documentDetailDto = new DocumentDetailDto();
        documentDetailDto.setFileName(file.getOriginalFilename());
        documentDetailDto.setFileId(fileName);
        documentDetailDto.setFileBase64(String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8)));
        return documentDetailDto;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }


    public DocumentDetailDto upload(MultipartFile multipartFile) {

        try {
            String fileName = multipartFile.getOriginalFilename();
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));


            return this.uploadFile(multipartFile, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }

    }

    public String download(String fileName) throws IOException {
        String destFileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));
        String destFilePath = "D:\\New folder" + destFileName;

        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("kumari-book-shop-inventory-firebase-adminsdk-yod2u-e261c2a8ae.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        Blob blob = storage.get(BlobId.of("kumari-book-shop-inventory.appspot.com", fileName));
        blob.downloadTo(Paths.get(destFilePath));
        return "Successfully Downloaded!";
    }

    public Boolean delete(String fileName) throws IOException {
        this.deleteFile("kumari-book-shop-inventory.appspot.com",fileName);
        return true;
    }

    public DocumentDetailDto update(MultipartFile file,String fileName) throws IOException {
       return this.updateFile("kumari-book-shop-inventory.appspot.com",fileName,file);
    }


}
