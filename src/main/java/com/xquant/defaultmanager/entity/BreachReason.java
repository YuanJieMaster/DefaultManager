package com.xquant.defaultmanager.entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "breach_reason")
@Data
public class BreachReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "reason_content", nullable = false, length = 1000)
    private String reasonContent;
    
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}