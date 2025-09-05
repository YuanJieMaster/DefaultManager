package com.xquant.defaultmanager.repository;

import com.xquant.defaultmanager.entity.BreachReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreachReasonRepository extends JpaRepository<BreachReason, Long> {
    // 根据是否启用查询违约原因
    List<BreachReason> findByIsEnabled(Boolean isEnabled);
    
    // 根据排序号升序查询
    List<BreachReason> findAllByOrderBySortOrderAsc();
    
    // 查询启用的违约原因并按排序号升序排列
    List<BreachReason> findByIsEnabledOrderBySortOrderAsc(Boolean isEnabled);
}