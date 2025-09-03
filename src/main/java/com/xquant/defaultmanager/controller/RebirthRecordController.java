package com.xquant.defaultmanager.controller;

import com.xquant.defaultmanager.dto.RebirthRecordDTO;
import com.xquant.defaultmanager.dto.RebirthRecordResponseDTO;
import com.xquant.defaultmanager.entity.RebirthRecord;
import com.xquant.defaultmanager.service.RebirthRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 重生管理
 * 提供客户重生申请的创建、查询、审核和删除等操作
 */
@RestController
@RequestMapping("/rebirth-records")
@RequiredArgsConstructor
public class RebirthRecordController {
    
    private final RebirthRecordService rebirthRecordService;
    
    /**
     * 创建重生申请
     * @param rebirthRecordDTO 重生申请信息
     * @return 创建成功的重生申请记录
     */
    @PostMapping
    public ResponseEntity<RebirthRecordResponseDTO> createRebirthRecord(@RequestBody RebirthRecordDTO rebirthRecordDTO) {
        RebirthRecordResponseDTO response = rebirthRecordService.createRebirthRecord(rebirthRecordDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 根据ID查询重生记录
     * @param id 重生记录ID
     * @return 重生记录信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<RebirthRecordResponseDTO> getRebirthRecordById(@PathVariable Long id) {
        RebirthRecordResponseDTO response = rebirthRecordService.getRebirthRecordById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据客户ID查询重生记录
     * @param customerId 客户ID
     * @return 该客户的所有重生申请记录列表
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<RebirthRecordResponseDTO>> getRebirthRecordsByCustomerId(@PathVariable Long customerId) {
        List<RebirthRecordResponseDTO> response = rebirthRecordService.getRebirthRecordsByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据违约记录ID查询重生记录
     * @param breachId 违约记录ID
     * @return 基于该违约记录的重生申请记录列表
     */
    @GetMapping("/breach/{breachId}")
    public ResponseEntity<List<RebirthRecordResponseDTO>> getRebirthRecordsByBreachId(@PathVariable Long breachId) {
        List<RebirthRecordResponseDTO> response = rebirthRecordService.getRebirthRecordsByBreachId(breachId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据审核状态查询重生记录
     * @param status 审核状态（待审核/已通过/已拒绝）
     * @return 指定状态的重生申请记录列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RebirthRecordResponseDTO>> getRebirthRecordsByStatus(@PathVariable RebirthRecord.ReviewStatus status) {
        List<RebirthRecordResponseDTO> response = rebirthRecordService.getRebirthRecordsByStatus(status);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 审核重生申请
     * @param id 重生记录ID
     * @param status 审核状态（通过/拒绝）
     * @param reviewerId 审核人ID
     * @return 审核后的重生申请记录信息
     */
    @PutMapping("/{id}/review")
    public ResponseEntity<RebirthRecordResponseDTO> reviewRebirthRecord(
            @PathVariable Long id,
            @RequestParam RebirthRecord.ReviewStatus status,
            @RequestParam Long reviewerId) {
        RebirthRecordResponseDTO response = rebirthRecordService.reviewRebirthRecord(id, status, reviewerId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除重生记录
     * @param id 重生记录ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRebirthRecord(@PathVariable Long id) {
        rebirthRecordService.deleteRebirthRecord(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 检查客户是否可以申请重生
     * @param customerId 客户ID
     * @return true表示可以申请，false表示不能申请
     */
    @GetMapping("/customer/{customerId}/can-rebirth")
    public ResponseEntity<Boolean> canCustomerRebirth(@PathVariable Long customerId) {
        boolean canRebirth = rebirthRecordService.canCustomerRebirth(customerId);
        return ResponseEntity.ok(canRebirth);
    }
}
