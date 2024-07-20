package com.g1.kumaribookshopbackend.entity;

import com.g1.kumaribookshopbackend.dto.DocumentDetailDto;
import com.g1.kumaribookshopbackend.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentDetail extends SuperEntity<DocumentDetailDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;
    @Column
    private String fileId;
    @Column
    private String fileName;
    @Column
    private String image;

    @OneToOne(targetEntity = Product.class, mappedBy = "documentDetail" ,fetch = FetchType.LAZY)
    private Product product;

    @Enumerated(EnumType.STRING)
    private RecordStatus recordStatus = RecordStatus.ACTIVE;

    @Override
    public DocumentDetailDto toDto() {
        DocumentDetailDto documentDetailDto = new DocumentDetailDto();
        documentDetailDto.setDocumentId(this.documentId);
        documentDetailDto.setFileId(this.getFileId());
        documentDetailDto.setFileName(this.fileName);
        documentDetailDto.setFileBase64(image);
        return documentDetailDto;
    }
}
