package com.xquant.defaultmanager.service.impl;

import com.xquant.defaultmanager.dto.RebirthRecordDTO;
import com.xquant.defaultmanager.dto.RebirthRecordResponseDTO;
import com.xquant.defaultmanager.entity.BreachRecord;
import com.xquant.defaultmanager.entity.Customer;
import com.xquant.defaultmanager.entity.RebirthRecord;
import com.xquant.defaultmanager.entity.RebirthReason;
import com.xquant.defaultmanager.repository.BreachRecordRepository;
import com.xquant.defaultmanager.repository.CustomerRepository;
import com.xquant.defaultmanager.repository.RebirthRecordRepository;
import com.xquant.defaultmanager.repository.RebirthReasonRepository;
import com.xquant.defaultmanager.service.RebirthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RebirthRecordServiceImpl implements RebirthRecordService {
    
    private final RebirthRecordRepository rebirthRecordRepository;
    private final BreachRecordRepository breachRecordRepository;
    private final CustomerRepository customerRepository;
    private final RebirthReasonRepository rebirthReasonRepository;
    
    @Override
    public RebirthRecordResponseDTO createRebirthRecord(RebirthRecordDTO rebirthRecordDTO) {
        // 验证客户是否存在
        Customer customer = customerRepository.findById(rebirthRecordDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        // 验证违约记录是否存在
        BreachRecord breachRecord = breachRecordRepository.findById(rebirthRecordDTO.getBreachId())
                .orElseThrow(() -> new RuntimeException("Breach record not found"));
        
        // 检查客户是否可以申请重生
        if (!canCustomerRebirth(customer.getId())) {
            throw new RuntimeException("Customer cannot apply for rebirth at this time");
        }
        
        // 验证重生原因是否存在
        RebirthReason rebirthReason = rebirthReasonRepository.findById(rebirthRecordDTO.getRebirthReasonId())
                .orElseThrow(() -> new RuntimeException("Rebirth reason not found"));
        
        RebirthRecord rebirthRecord = new RebirthRecord();
        rebirthRecord.setBreachRecord(breachRecord);
        rebirthRecord.setCustomer(customer);
        rebirthRecord.setRebirthReason(rebirthReason);
        rebirthRecord.setApplicantId(rebirthRecordDTO.getApplicantId());
        rebirthRecord.setStatus(RebirthRecord.ReviewStatus.PENDING);
        rebirthRecord.setCreateTime(LocalDateTime.now());
        
        RebirthRecord savedRecord = rebirthRecordRepository.save(rebirthRecord);
        return convertToResponseDTO(savedRecord);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RebirthRecordResponseDTO getRebirthRecordById(Long id) {
        RebirthRecord rebirthRecord = rebirthRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rebirth record not found"));
        return convertToResponseDTO(rebirthRecord);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RebirthRecordResponseDTO> getRebirthRecordsByCustomerId(Long customerId) {
        List<RebirthRecord> records = rebirthRecordRepository.findByCustomerId(customerId);
        return records.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RebirthRecordResponseDTO> getRebirthRecordsByBreachId(Long breachId) {
        List<RebirthRecord> records = rebirthRecordRepository.findByBreachRecordId(breachId);
        return records.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RebirthRecordResponseDTO> getRebirthRecordsByStatus(RebirthRecord.ReviewStatus status) {
        List<RebirthRecord> records = rebirthRecordRepository.findByStatus(status);
        return records.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public RebirthRecordResponseDTO reviewRebirthRecord(Long id, RebirthRecord.ReviewStatus status, Long reviewerId) {
        RebirthRecord rebirthRecord = rebirthRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rebirth record not found"));
        
        rebirthRecord.setStatus(status);
        rebirthRecord.setReviewerId(reviewerId);
        rebirthRecord.setReviewTime(LocalDateTime.now());
        
        // 如果审核通过，更新客户的违约状态为 false（重生成功）
        if (status == RebirthRecord.ReviewStatus.APPROVED) {
            Customer customer = rebirthRecord.getCustomer();
            customer.setIsBreached(false);
            customerRepository.save(customer);
        }
        
        RebirthRecord savedRecord = rebirthRecordRepository.save(rebirthRecord);
        return convertToResponseDTO(savedRecord);
    }
    
    @Override
    public void deleteRebirthRecord(Long id) {
        rebirthRecordRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canCustomerRebirth(Long customerId) {
        // 检查客户是否有违约记录
        List<BreachRecord> breachRecords = breachRecordRepository.findByCustomerId(customerId);
        if (breachRecords.isEmpty()) {
            return false; // 没有违约记录，不能申请重生
        }
        
        // 检查是否已经有待审核或已批准的重生申请
        List<RebirthRecord> pendingRebirths = rebirthRecordRepository.findByCustomerIdAndStatus(
                customerId, RebirthRecord.ReviewStatus.PENDING);
        List<RebirthRecord> approvedRebirths = rebirthRecordRepository.findByCustomerIdAndStatus(
                customerId, RebirthRecord.ReviewStatus.APPROVED);
        
        return pendingRebirths.isEmpty() && approvedRebirths.isEmpty();
    }
    
    private RebirthRecordResponseDTO convertToResponseDTO(RebirthRecord rebirthRecord) {
        RebirthRecordResponseDTO dto = new RebirthRecordResponseDTO();
        dto.setId(rebirthRecord.getId());
        dto.setBreachId(rebirthRecord.getBreachRecord().getId());
        dto.setCustomerId(rebirthRecord.getCustomer().getId());
        dto.setCustomerName(rebirthRecord.getCustomer().getName());
        dto.setRebirthReasonId(rebirthRecord.getRebirthReason().getId());
        dto.setRebirthReasonContent(rebirthRecord.getRebirthReason().getReasonContent());
        dto.setReason(rebirthRecord.getRebirthReason().getReasonContent()); // 向后兼容
        dto.setApplicantId(rebirthRecord.getApplicantId());
        dto.setReviewerId(rebirthRecord.getReviewerId());
        dto.setStatus(rebirthRecord.getStatus());
        dto.setReviewTime(rebirthRecord.getReviewTime());
        dto.setCreateTime(rebirthRecord.getCreateTime());
        return dto;
    }
}
