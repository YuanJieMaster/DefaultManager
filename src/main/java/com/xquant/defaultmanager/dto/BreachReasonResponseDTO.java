package com.xquant.defaultmanager.dto;

import lombok.Data;

@Data
public class BreachReasonResponseDTO {
    private Long id;
    private String reasonContent;
    private Boolean isEnabled;
    private Integer sortOrder;
}