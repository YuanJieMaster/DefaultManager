package com.xquant.defaultmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rebirth_reason")
public class RebirthReason {
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