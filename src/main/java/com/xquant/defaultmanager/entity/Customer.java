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
}
