package com.g1.kumaribookshopbackend.service.impl;

import com.g1.kumaribookshopbackend.dto.DocumentDetailDto;
import com.g1.kumaribookshopbackend.exception.InternalServerException;
import com.g1.kumaribookshopbackend.util.MessageConstant;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class GoogleDriveService {


    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "kumari-book-shop";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "google-drive-client-credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        // Load client secrets.
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("kumari-admin");
        //returns an authorized Credential object.
        return credential;
    }

    public Drive getInstance() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }



    public String getFileContentAsBase64(String fileId) {
        try {
            InputStream inputStream = getInstance().files().get(fileId).executeMediaAsInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] fileBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            log.error("Error retrieving file content: " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }


    public DocumentDetailDto uploadFile(MultipartFile file) {
        try {
            String folderId = "1NTHUMhPPCz_BAp5RgS8POTPayd5PRyum";

            File fileMetadata = new File();
            fileMetadata.setParents(Collections.singletonList(folderId));
            fileMetadata.setName(file.getOriginalFilename());
            File uploadFile = getInstance()
                    .files()
                    .create(fileMetadata, new InputStreamContent(
                            file.getContentType(),
                            new ByteArrayInputStream(file.getBytes()))

                    )
                    .setFields("id").execute();

            if (Objects.nonNull(uploadFile)) {
                DocumentDetailDto documentDetailDto = new DocumentDetailDto();
                documentDetailDto.setFileId(uploadFile.getId());
                documentDetailDto.setFileName(file.getOriginalFilename());
                return documentDetailDto;
            } else {
                throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            log.error("uploadFile() | google drive upload failed : " + e.getMessage());
            throw new InternalServerException(e.getMessage());
        }
    }

    public DocumentDetailDto updateFile(MultipartFile file, String fileId) {
        try {

            File fileMetadata = new File();
            fileMetadata.setName(file.getOriginalFilename());
            fileMetadata .setDescription("This file was updated");

            File updateFile = getInstance()
                    .files()
                    .update(fileId,
                            fileMetadata,
                            new InputStreamContent(
                                    file.getContentType(),
                                    new ByteArrayInputStream(file.getBytes()))
                    )
                    .setFields("id").execute();

            if (Objects.nonNull(updateFile)) {
                DocumentDetailDto documentDetailDto = new DocumentDetailDto();
                documentDetailDto.setFileId(updateFile.getId());
                documentDetailDto.setFileName(file.getOriginalFilename());
                return documentDetailDto;

            } else {
                throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            log.error("updateFile() | google drive update failed : " + e.getMessage());
            throw new InternalServerException(e.getMessage());
        }
    }


    public Boolean deleteFile(String fileId) {
        try {

            getInstance()
                    .files()
                    .delete(fileId).execute();
            return true;

        } catch (Exception e) {
            log.error("deleteFile() | google drive update failed : " + e.getMessage());
            throw new InternalServerException(e.getMessage());
        }
    }


    public DocumentDetailDto getFile(String fileId) {
        try {

            File file = getInstance().files().get(fileId).execute();

            if (Objects.nonNull(file)) {
                DocumentDetailDto documentDetailDto = new DocumentDetailDto();
                documentDetailDto.setFileId(file.getId());
                documentDetailDto.setFileName(file.getName());
                return documentDetailDto;
            } else {
                throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            log.error("uploadFile() | google drive upload failed : " + e.getMessage());
            throw new InternalServerException(e.getMessage());
        }
    }



}
