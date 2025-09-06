package com.xquant.defaultmanager.dto;

import lombok.Data;
import com.xquant.defaultmanager.entity.BreachRecord.Severity;

@Data
public class BreachRecordDTO {
    
    private Long customerId;
    private Long breachReasonId;
    private Severity severity;
    private Long applicantId;
}
