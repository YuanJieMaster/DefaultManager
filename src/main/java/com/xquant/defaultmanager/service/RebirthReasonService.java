package com.xquant.defaultmanager.service;

import com.xquant.defaultmanager.dto.RebirthReasonDTO;
import com.xquant.defaultmanager.dto.RebirthReasonResponseDTO;

import java.util.List;

public interface RebirthReasonService {
    // 创建新的重生原因
    RebirthReasonResponseDTO createRebirthReason(RebirthReasonDTO rebirthReasonDTO);
    
    // 根据ID获取重生原因
    RebirthReasonResponseDTO getRebirthReasonById(Long id);
    
    // 获取所有重生原因
    List<RebirthReasonResponseDTO> getAllRebirthReasons();
    
    // 获取启用的重生原因
    List<RebirthReasonResponseDTO> getEnabledRebirthReasons();
    
    // 更新重生原因
    RebirthReasonResponseDTO updateRebirthReason(Long id, RebirthReasonDTO rebirthReasonDTO);
    
    // 删除重生原因
    void deleteRebirthReason(Long id);
    
    // 启用/禁用重生原因
    void toggleRebirthReasonStatus(Long id);
}