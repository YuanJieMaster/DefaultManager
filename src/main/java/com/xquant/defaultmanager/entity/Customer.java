package com.xquant.defaultmanager.entity;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "industry", length = 50)
    private String industry;

    @Column(name = "region", length = 50)
    private String region;

    @Column(name = "external_rating", length = 20)
    private String externalRating;

    @Column(name = "is_breached")
    private Boolean isBreached = false;

    @Column(name = "create_time")
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
