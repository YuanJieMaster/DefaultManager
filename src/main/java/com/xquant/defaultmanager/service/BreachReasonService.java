package com.xquant.defaultmanager.service;

import com.xquant.defaultmanager.dto.BreachReasonDTO;
import com.xquant.defaultmanager.dto.BreachReasonResponseDTO;

import java.util.List;

public interface BreachReasonService {
    // 创建新的违约原因
    BreachReasonResponseDTO createBreachReason(BreachReasonDTO breachReasonDTO);
    
    // 根据ID获取违约原因
    BreachReasonResponseDTO getBreachReasonById(Long id);
    
    // 获取所有违约原因
    List<BreachReasonResponseDTO> getAllBreachReasons();
    
    // 获取启用的违约原因
    List<BreachReasonResponseDTO> getEnabledBreachReasons();
    
    // 更新违约原因
    BreachReasonResponseDTO updateBreachReason(Long id, BreachReasonDTO breachReasonDTO);
    
    // 删除违约原因
    void deleteBreachReason(Long id);
    
    // 启用/禁用违约原因
    void toggleBreachReasonStatus(Long id);
}