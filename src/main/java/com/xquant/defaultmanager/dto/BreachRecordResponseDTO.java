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
    private Long breachReasonId; // 关联的违约原因ID
    private String breachReasonContent; // 违约原因内容
    private String reason; // 为了向后兼容保留的字段，实际使用breachReasonContent
    private Severity severity;
    private Long applicantId;
    private Long reviewerId;
    private ReviewStatus status;
    private LocalDateTime reviewTime;
    private LocalDateTime createTime;
}
