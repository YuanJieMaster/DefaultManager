package com.xquant.defaultmanager.controller;

import com.xquant.defaultmanager.dto.RebirthReasonDTO;
import com.xquant.defaultmanager.dto.RebirthReasonResponseDTO;
import com.xquant.defaultmanager.service.RebirthReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/rebirth-reasons")
@RequiredArgsConstructor
public class RebirthReasonController {

    private final RebirthReasonService rebirthReasonService;

    // 创建新的重生原因
    @PostMapping
    public ResponseEntity<RebirthReasonResponseDTO> createRebirthReason(@Valid @RequestBody RebirthReasonDTO rebirthReasonDTO) {
        RebirthReasonResponseDTO createdReason = rebirthReasonService.createRebirthReason(rebirthReasonDTO);
        return new ResponseEntity<>(createdReason, HttpStatus.CREATED);
    }

    // 根据ID获取重生原因
    @GetMapping("/{id}")
    public ResponseEntity<RebirthReasonResponseDTO> getRebirthReasonById(@PathVariable Long id) {
        RebirthReasonResponseDTO reason = rebirthReasonService.getRebirthReasonById(id);
        return ResponseEntity.ok(reason);
    }

    // 获取所有重生原因
    @GetMapping
    public ResponseEntity<List<RebirthReasonResponseDTO>> getAllRebirthReasons() {
        List<RebirthReasonResponseDTO> reasons = rebirthReasonService.getAllRebirthReasons();
        return ResponseEntity.ok(reasons);
    }

    // 获取启用的重生原因
    @GetMapping("/enabled")
    public ResponseEntity<List<RebirthReasonResponseDTO>> getEnabledRebirthReasons() {
        List<RebirthReasonResponseDTO> reasons = rebirthReasonService.getEnabledRebirthReasons();
        return ResponseEntity.ok(reasons);
    }

    // 更新重生原因
    @PutMapping("/{id}")
    public ResponseEntity<RebirthReasonResponseDTO> updateRebirthReason(
            @PathVariable Long id,
            @Valid @RequestBody RebirthReasonDTO rebirthReasonDTO) {
        RebirthReasonResponseDTO updatedReason = rebirthReasonService.updateRebirthReason(id, rebirthReasonDTO);
        return ResponseEntity.ok(updatedReason);
    }

    // 删除重生原因
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRebirthReason(@PathVariable Long id) {
        rebirthReasonService.deleteRebirthReason(id);
        return ResponseEntity.noContent().build();
    }

    // 启用/禁用重生原因
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleRebirthReasonStatus(@PathVariable Long id) {
        rebirthReasonService.toggleRebirthReasonStatus(id);
        return ResponseEntity.noContent().build();
    }
}