package com.xquant.defaultmanager.service.impl;

import com.xquant.defaultmanager.dto.RebirthReasonDTO;
import com.xquant.defaultmanager.dto.RebirthReasonResponseDTO;
import com.xquant.defaultmanager.entity.RebirthReason;
import com.xquant.defaultmanager.repository.RebirthReasonRepository;
import com.xquant.defaultmanager.service.RebirthReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RebirthReasonServiceImpl implements RebirthReasonService {

    private final RebirthReasonRepository rebirthReasonRepository;

    @Override
    public RebirthReasonResponseDTO createRebirthReason(RebirthReasonDTO rebirthReasonDTO) {
        RebirthReason rebirthReason = new RebirthReason();
        rebirthReason.setReasonContent(rebirthReasonDTO.getReasonContent());
        rebirthReason.setIsEnabled(rebirthReasonDTO.getIsEnabled());
        rebirthReason.setSortOrder(rebirthReasonDTO.getSortOrder());
        
        RebirthReason savedReason = rebirthReasonRepository.save(rebirthReason);
        return convertToResponseDTO(savedReason);
    }

    @Override
    @Transactional(readOnly = true)
    public RebirthReasonResponseDTO getRebirthReasonById(Long id) {
        RebirthReason rebirthReason = rebirthReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rebirth reason not found"));
        return convertToResponseDTO(rebirthReason);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RebirthReasonResponseDTO> getAllRebirthReasons() {
        List<RebirthReason> rebirthReasons = rebirthReasonRepository.findAllByOrderBySortOrderAsc();
        return rebirthReasons.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RebirthReasonResponseDTO> getEnabledRebirthReasons() {
        List<RebirthReason> rebirthReasons = rebirthReasonRepository.findByIsEnabledOrderBySortOrderAsc(true);
        return rebirthReasons.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RebirthReasonResponseDTO updateRebirthReason(Long id, RebirthReasonDTO rebirthReasonDTO) {
        RebirthReason rebirthReason = rebirthReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rebirth reason not found"));
        
        rebirthReason.setReasonContent(rebirthReasonDTO.getReasonContent());
        rebirthReason.setIsEnabled(rebirthReasonDTO.getIsEnabled());
        rebirthReason.setSortOrder(rebirthReasonDTO.getSortOrder());
        
        RebirthReason updatedReason = rebirthReasonRepository.save(rebirthReason);
        return convertToResponseDTO(updatedReason);
    }

    @Override
    public void deleteRebirthReason(Long id) {
        RebirthReason rebirthReason = rebirthReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rebirth reason not found"));
        rebirthReasonRepository.delete(rebirthReason);
    }

    @Override
    public void toggleRebirthReasonStatus(Long id) {
        RebirthReason rebirthReason = rebirthReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rebirth reason not found"));
        rebirthReason.setIsEnabled(!rebirthReason.getIsEnabled());
        rebirthReasonRepository.save(rebirthReason);
    }

    // 辅助方法：将实体转换为响应DTO
    private RebirthReasonResponseDTO convertToResponseDTO(RebirthReason rebirthReason) {
        RebirthReasonResponseDTO dto = new RebirthReasonResponseDTO();
        dto.setId(rebirthReason.getId());
        dto.setReasonContent(rebirthReason.getReasonContent());
        dto.setIsEnabled(rebirthReason.getIsEnabled());
        dto.setSortOrder(rebirthReason.getSortOrder());
        return dto;
    }
}