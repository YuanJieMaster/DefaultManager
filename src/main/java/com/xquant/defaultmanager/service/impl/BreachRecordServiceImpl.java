package com.xquant.defaultmanager.service.impl;

import com.xquant.defaultmanager.dto.BreachRecordDTO;
import com.xquant.defaultmanager.dto.BreachRecordResponseDTO;
import com.xquant.defaultmanager.entity.BreachRecord;
import com.xquant.defaultmanager.entity.Customer;
import com.xquant.defaultmanager.repository.BreachRecordRepository;
import com.xquant.defaultmanager.repository.CustomerRepository;
import com.xquant.defaultmanager.service.BreachRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BreachRecordServiceImpl implements BreachRecordService {
    
    private final BreachRecordRepository breachRecordRepository;
    private final CustomerRepository customerRepository;
    
    @Override
    public BreachRecordResponseDTO createBreachRecord(BreachRecordDTO breachRecordDTO) {
        Customer customer = customerRepository.findById(breachRecordDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        BreachRecord breachRecord = new BreachRecord();
        breachRecord.setCustomer(customer);
        breachRecord.setReason(breachRecordDTO.getReason());
        breachRecord.setSeverity(breachRecordDTO.getSeverity());
        breachRecord.setApplicantId(breachRecordDTO.getApplicantId());
        breachRecord.setStatus(BreachRecord.ReviewStatus.PENDING);
        breachRecord.setCreateTime(LocalDateTime.now());
        
        BreachRecord savedRecord = breachRecordRepository.save(breachRecord);
        return convertToResponseDTO(savedRecord);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BreachRecordResponseDTO getBreachRecordById(Long id) {
        BreachRecord breachRecord = breachRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breach record not found"));
        return convertToResponseDTO(breachRecord);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BreachRecordResponseDTO> getBreachRecordsByCustomerId(Long customerId) {
        List<BreachRecord> records = breachRecordRepository.findByCustomerId(customerId);
        return records.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BreachRecordResponseDTO> getBreachRecordsByStatus(BreachRecord.ReviewStatus status) {
        List<BreachRecord> records = breachRecordRepository.findByStatus(status);
        return records.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BreachRecordResponseDTO> getBreachRecordsBySeverity(BreachRecord.Severity severity) {
        List<BreachRecord> records = breachRecordRepository.findBySeverity(severity);
        return records.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public BreachRecordResponseDTO reviewBreachRecord(Long id, BreachRecord.ReviewStatus status, Long reviewerId) {
        BreachRecord breachRecord = breachRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breach record not found"));
        
        breachRecord.setStatus(status);
        breachRecord.setReviewerId(reviewerId);
        breachRecord.setReviewTime(LocalDateTime.now());
        
        // 如果审核通过，更新客户的违约状态
        if (status == BreachRecord.ReviewStatus.APPROVED) {
            Customer customer = breachRecord.getCustomer();
            customer.setIsBreached(true);
            customerRepository.save(customer);
        }
        
        BreachRecord savedRecord = breachRecordRepository.save(breachRecord);
        return convertToResponseDTO(savedRecord);
    }
    
    @Override
    public void deleteBreachRecord(Long id) {
        breachRecordRepository.deleteById(id);
    }
    
    private BreachRecordResponseDTO convertToResponseDTO(BreachRecord breachRecord) {
        BreachRecordResponseDTO dto = new BreachRecordResponseDTO();
        dto.setId(breachRecord.getId());
        dto.setCustomerId(breachRecord.getCustomer().getId());
        dto.setCustomerName(breachRecord.getCustomer().getName());
        dto.setReason(breachRecord.getReason());
        dto.setSeverity(breachRecord.getSeverity());
        dto.setApplicantId(breachRecord.getApplicantId());
        dto.setReviewerId(breachRecord.getReviewerId());
        dto.setStatus(breachRecord.getStatus());
        dto.setReviewTime(breachRecord.getReviewTime());
        dto.setCreateTime(breachRecord.getCreateTime());
        return dto;
    }
}
