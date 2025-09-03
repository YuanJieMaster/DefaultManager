package com.xquant.defaultmanager.service;

import com.xquant.defaultmanager.dto.RebirthRecordDTO;
import com.xquant.defaultmanager.dto.RebirthRecordResponseDTO;
import com.xquant.defaultmanager.entity.RebirthRecord;

import java.util.List;

public interface RebirthRecordService {
    
    RebirthRecordResponseDTO createRebirthRecord(RebirthRecordDTO rebirthRecordDTO);
    
    RebirthRecordResponseDTO getRebirthRecordById(Long id);
    
    List<RebirthRecordResponseDTO> getRebirthRecordsByCustomerId(Long customerId);
    
    List<RebirthRecordResponseDTO> getRebirthRecordsByBreachId(Long breachId);
    
    List<RebirthRecordResponseDTO> getRebirthRecordsByStatus(RebirthRecord.ReviewStatus status);
    
    RebirthRecordResponseDTO reviewRebirthRecord(Long id, RebirthRecord.ReviewStatus status, Long reviewerId);
    
    void deleteRebirthRecord(Long id);
    
    boolean canCustomerRebirth(Long customerId);
}
