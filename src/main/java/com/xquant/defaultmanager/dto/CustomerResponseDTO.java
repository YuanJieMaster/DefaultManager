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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getExternalRating() {
        return externalRating;
    }

    public void setExternalRating(String externalRating) {
        this.externalRating = externalRating;
    }

    public Boolean getIsBreached() {
        return isBreached;
    }

    public void setIsBreached(Boolean breached) {
        isBreached = breached;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}