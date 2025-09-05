package com.xquant.defaultmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RebirthReasonDTO {
    @NotBlank(message = "重生原因不能为空")
    @Size(max = 1000, message = "重生原因不能超过1000个字符")
    private String reasonContent;
    
    private Boolean isEnabled = true;
    
    private Integer sortOrder = 0;
}