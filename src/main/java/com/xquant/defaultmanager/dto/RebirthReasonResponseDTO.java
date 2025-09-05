package com.xquant.defaultmanager.dto;

import lombok.Data;

@Data
public class RebirthReasonResponseDTO {
    private Long id;
    private String reasonContent;
    private Boolean isEnabled;
    private Integer sortOrder;
}