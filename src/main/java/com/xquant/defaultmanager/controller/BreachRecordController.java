package com.xquant.defaultmanager.controller;

import com.xquant.defaultmanager.dto.BreachRecordDTO;
import com.xquant.defaultmanager.dto.BreachRecordResponseDTO;
import com.xquant.defaultmanager.entity.BreachRecord;
import com.xquant.defaultmanager.service.BreachRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 违约管理
 * 提供违约记录的创建、查询、审核和删除等操作
 */
@RestController
@RequestMapping("/api/breach-records")
@RequiredArgsConstructor
public class BreachRecordController {
    
    private final BreachRecordService breachRecordService;
    
    /**
     * 创建违约记录
     * @param breachRecordDTO 违约记录信息
     * @return 创建成功的违约记录
     */
    @PostMapping
    public ResponseEntity<BreachRecordResponseDTO> createBreachRecord(@RequestBody BreachRecordDTO breachRecordDTO) {
        BreachRecordResponseDTO response = breachRecordService.createBreachRecord(breachRecordDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 根据ID查询违约记录
     * @param id 违约记录ID
     * @return 违约记录信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<BreachRecordResponseDTO> getBreachRecordById(@PathVariable Long id) {
        BreachRecordResponseDTO response = breachRecordService.getBreachRecordById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据客户ID查询违约记录
     * @param customerId 客户ID
     * @return 该客户的所有违约记录列表
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BreachRecordResponseDTO>> getBreachRecordsByCustomerId(@PathVariable Long customerId) {
        List<BreachRecordResponseDTO> response = breachRecordService.getBreachRecordsByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据审核状态查询违约记录
     * @param status 审核状态（待审核/已通过/已拒绝）
     * @return 指定状态的违约记录列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BreachRecordResponseDTO>> getBreachRecordsByStatus(@PathVariable BreachRecord.ReviewStatus status) {
        List<BreachRecordResponseDTO> response = breachRecordService.getBreachRecordsByStatus(status);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据严重程度查询违约记录
     * @param severity 严重程度（高/中/低）
     * @return 指定严重程度的违约记录列表
     */
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<BreachRecordResponseDTO>> getBreachRecordsBySeverity(@PathVariable BreachRecord.Severity severity) {
        List<BreachRecordResponseDTO> response = breachRecordService.getBreachRecordsBySeverity(severity);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 审核违约记录
     * @param id 违约记录ID
     * @param status 审核状态（通过/拒绝）
     * @param reviewerId 审核人ID
     * @return 审核后的违约记录信息
     */
    @PutMapping("/{id}/review")
    public ResponseEntity<BreachRecordResponseDTO> reviewBreachRecord(
            @PathVariable Long id,
            @RequestParam BreachRecord.ReviewStatus status,
            @RequestParam Long reviewerId) {
        BreachRecordResponseDTO response = breachRecordService.reviewBreachRecord(id, status, reviewerId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除违约记录
     * @param id 违约记录ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBreachRecord(@PathVariable Long id) {
        breachRecordService.deleteBreachRecord(id);
        return ResponseEntity.noContent().build();
    }
}
