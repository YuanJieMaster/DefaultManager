package com.xquant.defaultmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerDTO {
    
    @NotBlank(message = "客户名称不能为空")
    @Size(max = 100, message = "客户名称长度不能超过100个字符")
    private String name;
    
    @Size(max = 50, message = "行业长度不能超过50个字符")
    private String industry;
    
    @Size(max = 50, message = "地区长度不能超过50个字符")
    private String region;
    
    @Size(max = 20, message = "外部评级长度不能超过20个字符")
    private String externalRating;
    
    private Boolean isBreached = false;
}