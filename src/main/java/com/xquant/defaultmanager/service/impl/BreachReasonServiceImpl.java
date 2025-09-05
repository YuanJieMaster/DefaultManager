package com.xquant.defaultmanager.service.impl;

import com.xquant.defaultmanager.dto.BreachReasonDTO;
import com.xquant.defaultmanager.dto.BreachReasonResponseDTO;
import com.xquant.defaultmanager.entity.BreachReason;
import com.xquant.defaultmanager.repository.BreachReasonRepository;
import com.xquant.defaultmanager.service.BreachReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BreachReasonServiceImpl implements BreachReasonService {

    private final BreachReasonRepository breachReasonRepository;

    @Override
    public BreachReasonResponseDTO createBreachReason(BreachReasonDTO breachReasonDTO) {
        BreachReason breachReason = new BreachReason();
        breachReason.setReasonContent(breachReasonDTO.getReasonContent());
        breachReason.setIsEnabled(breachReasonDTO.getIsEnabled());
        breachReason.setSortOrder(breachReasonDTO.getSortOrder());
        
        BreachReason savedReason = breachReasonRepository.save(breachReason);
        return convertToResponseDTO(savedReason);
    }

    @Override
    @Transactional(readOnly = true)
    public BreachReasonResponseDTO getBreachReasonById(Long id) {
        BreachReason breachReason = breachReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breach reason not found"));
        return convertToResponseDTO(breachReason);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BreachReasonResponseDTO> getAllBreachReasons() {
        List<BreachReason> breachReasons = breachReasonRepository.findAllByOrderBySortOrderAsc();
        return breachReasons.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BreachReasonResponseDTO> getEnabledBreachReasons() {
        List<BreachReason> breachReasons = breachReasonRepository.findByIsEnabledOrderBySortOrderAsc(true);
        return breachReasons.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BreachReasonResponseDTO updateBreachReason(Long id, BreachReasonDTO breachReasonDTO) {
        BreachReason breachReason = breachReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breach reason not found"));
        
        breachReason.setReasonContent(breachReasonDTO.getReasonContent());
        breachReason.setIsEnabled(breachReasonDTO.getIsEnabled());
        breachReason.setSortOrder(breachReasonDTO.getSortOrder());
        
        BreachReason updatedReason = breachReasonRepository.save(breachReason);
        return convertToResponseDTO(updatedReason);
    }

    @Override
    public void deleteBreachReason(Long id) {
        BreachReason breachReason = breachReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breach reason not found"));
        breachReasonRepository.delete(breachReason);
    }

    @Override
    public void toggleBreachReasonStatus(Long id) {
        BreachReason breachReason = breachReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breach reason not found"));
        breachReason.setIsEnabled(!breachReason.getIsEnabled());
        breachReasonRepository.save(breachReason);
    }

    // 辅助方法：将实体转换为响应DTO
    private BreachReasonResponseDTO convertToResponseDTO(BreachReason breachReason) {
        BreachReasonResponseDTO dto = new BreachReasonResponseDTO();
        dto.setId(breachReason.getId());
        dto.setReasonContent(breachReason.getReasonContent());
        dto.setIsEnabled(breachReason.getIsEnabled());
        dto.setSortOrder(breachReason.getSortOrder());
        return dto;
    }
}