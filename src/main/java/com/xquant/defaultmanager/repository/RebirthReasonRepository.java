package com.xquant.defaultmanager.repository;

import com.xquant.defaultmanager.entity.RebirthReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RebirthReasonRepository extends JpaRepository<RebirthReason, Long> {
    // 根据是否启用查询重生原因
    List<RebirthReason> findByIsEnabled(Boolean isEnabled);
    
    // 根据排序号升序查询
    List<RebirthReason> findAllByOrderBySortOrderAsc();
    
    // 查询启用的重生原因并按排序号升序排列
    List<RebirthReason> findByIsEnabledOrderBySortOrderAsc(Boolean isEnabled);
}