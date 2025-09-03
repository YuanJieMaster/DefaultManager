package com.xquant.defaultmanager.dto;

import lombok.Data;
import com.xquant.defaultmanager.entity.BreachRecord.Severity;
import com.xquant.defaultmanager.entity.BreachRecord.ReviewStatus;

import java.time.LocalDateTime;

@Data
public class BreachRecordResponseDTO {
    
    private Long id;
    private Long customerId;
    private String customerName;
    private String reason;
    private Severity severity;
    private Long applicantId;
    private Long reviewerId;
    private ReviewStatus status;
    private LocalDateTime reviewTime;
    private LocalDateTime createTime;
}
