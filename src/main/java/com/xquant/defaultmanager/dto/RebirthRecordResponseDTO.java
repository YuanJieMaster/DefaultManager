package com.xquant.defaultmanager.dto;

import lombok.Data;
import com.xquant.defaultmanager.entity.RebirthRecord.ReviewStatus;

import java.time.LocalDateTime;

@Data
public class RebirthRecordResponseDTO {
    
    private Long id;
    private Long breachId;
    private Long customerId;
    private String customerName;
    private Long rebirthReasonId;
    private String rebirthReasonContent;
    private String reason; // 向后兼容
    private Long applicantId;
    private Long reviewerId;
    private ReviewStatus status;
    private LocalDateTime reviewTime;
    private LocalDateTime createTime;
}
