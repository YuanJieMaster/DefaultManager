package com.xquant.defaultmanager.dto;

import lombok.Data;

@Data
public class RebirthRecordDTO {
    
    private Long breachId;
    private Long customerId;
    private String reason;
    private Long applicantId;
}
