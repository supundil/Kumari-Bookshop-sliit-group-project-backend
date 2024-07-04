package com.g1.kumaribookshopbackend.entity;

import com.g1.kumaribookshopbackend.dto.SuperDto;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class SuperEntity<D extends SuperDto> implements Serializable {

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @UpdateTimestamp
    @Column
    private LocalDateTime modifiedDate;

    public abstract D toDto();
}
