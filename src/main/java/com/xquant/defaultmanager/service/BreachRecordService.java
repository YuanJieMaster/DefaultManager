package com.xquant.defaultmanager.service;

import com.xquant.defaultmanager.dto.BreachRecordDTO;
import com.xquant.defaultmanager.dto.BreachRecordResponseDTO;
import com.xquant.defaultmanager.entity.BreachRecord;

import java.util.List;

public interface BreachRecordService {
    
    BreachRecordResponseDTO createBreachRecord(BreachRecordDTO breachRecordDTO);
    
    BreachRecordResponseDTO getBreachRecordById(Long id);
    
    List<BreachRecordResponseDTO> getBreachRecordsByCustomerId(Long customerId);
    
    List<BreachRecordResponseDTO> getBreachRecordsByStatus(BreachRecord.ReviewStatus status);
    
    List<BreachRecordResponseDTO> getBreachRecordsBySeverity(BreachRecord.Severity severity);
    
    BreachRecordResponseDTO reviewBreachRecord(Long id, BreachRecord.ReviewStatus status, Long reviewerId);
    
    void deleteBreachRecord(Long id);
}
