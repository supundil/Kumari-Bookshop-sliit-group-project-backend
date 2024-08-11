package com.g1.kumaribookshopbackend.dto;

import com.g1.kumaribookshopbackend.entity.DocumentDetail;
import lombok.Data;

@Data
public class DocumentDetailDto extends SuperDto<DocumentDetail> {
    private Long documentId;
    private String fileId;
    private String fileName;
    private String fileBase64;

    @Override
    public DocumentDetail toEntity() {
        DocumentDetail documentDetail = new DocumentDetail();
        documentDetail.setDocumentId(this.documentId);
        documentDetail.setFileId(this.fileId);
        documentDetail.setFileName(this.fileName);
        documentDetail.setImage(this.fileBase64);
        return documentDetail;
    }
}
