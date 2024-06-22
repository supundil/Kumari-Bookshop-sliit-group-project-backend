package com.g1.kumaribookshopbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.g1.kumaribookshopbackend.entity.SuperEntity;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@MappedSuperclass
public abstract class SuperDto<E extends SuperEntity> implements Serializable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime modifiedDate;

    public abstract E toEntity();
}
