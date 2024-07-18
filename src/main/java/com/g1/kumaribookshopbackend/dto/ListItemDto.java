package com.g1.kumaribookshopbackend.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ListItemDto {
    private Object value;
    private String label;

    public ListItemDto() {
    }

    public ListItemDto(Object value, String label) {
        this.value = value;
        this.label = label;
    }
}
