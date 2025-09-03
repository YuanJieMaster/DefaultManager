package com.xquant.defaultmanager.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerResponseDTO {
    
    private Long id;
    private String name;
    private String industry;
    private String region;
    private String externalRating;
    private Boolean isBreached;
    private LocalDateTime createTime;
}