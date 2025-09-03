package com.xquant.defaultmanager.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "breach_record")
@EntityListeners(AuditingEntityListener.class)
public class BreachRecord {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
  
    @Column(name = "reason", nullable = false, length = 500)
    private String reason;
  
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity;
  
    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;
  
    @Column(name = "reviewer_id")
    private Long reviewerId;
  
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReviewStatus status = ReviewStatus.PENDING;
  
    @Column(name = "review_time")
    private LocalDateTime reviewTime;
  
    @CreatedDate
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
  
    public enum Severity {
        HIGH, MEDIUM, LOW
    }
    
    public enum ReviewStatus {
        PENDING("待审核"),
        APPROVED("已通过"),
        REJECTED("已拒绝");
      
        private final String description;
      
        ReviewStatus(String description) {
            this.description = description;
        }
      
        public String getDescription() {
            return description;
        }
    }
}
