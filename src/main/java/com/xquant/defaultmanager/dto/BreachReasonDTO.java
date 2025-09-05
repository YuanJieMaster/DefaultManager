package com.xquant.defaultmanager.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class BreachReasonDTO {
    @NotBlank(message = "违约原因不能为空")
    @Size(max = 1000, message = "违约原因不能超过1000个字符")
    private String reasonContent;
    
    private Boolean isEnabled = true;
    
    private Integer sortOrder = 0;
}