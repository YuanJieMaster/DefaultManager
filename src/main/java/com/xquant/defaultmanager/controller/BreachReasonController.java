package com.xquant.defaultmanager.controller;

import com.xquant.defaultmanager.dto.BreachReasonDTO;
import com.xquant.defaultmanager.dto.BreachReasonResponseDTO;
import com.xquant.defaultmanager.service.BreachReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/breach-reasons")
@RequiredArgsConstructor
public class BreachReasonController {

    private final BreachReasonService breachReasonService;

    // 创建新的违约原因
    @PostMapping
    public ResponseEntity<BreachReasonResponseDTO> createBreachReason(@Valid @RequestBody BreachReasonDTO breachReasonDTO) {
        BreachReasonResponseDTO createdReason = breachReasonService.createBreachReason(breachReasonDTO);
        return new ResponseEntity<>(createdReason, HttpStatus.CREATED);
    }

    // 根据ID获取违约原因
    @GetMapping("/{id}")
    public ResponseEntity<BreachReasonResponseDTO> getBreachReasonById(@PathVariable Long id) {
        BreachReasonResponseDTO reason = breachReasonService.getBreachReasonById(id);
        return ResponseEntity.ok(reason);
    }

    // 获取所有违约原因
    @GetMapping
    public ResponseEntity<List<BreachReasonResponseDTO>> getAllBreachReasons() {
        List<BreachReasonResponseDTO> reasons = breachReasonService.getAllBreachReasons();
        return ResponseEntity.ok(reasons);
    }

    // 获取启用的违约原因
    @GetMapping("/enabled")
    public ResponseEntity<List<BreachReasonResponseDTO>> getEnabledBreachReasons() {
        List<BreachReasonResponseDTO> reasons = breachReasonService.getEnabledBreachReasons();
        return ResponseEntity.ok(reasons);
    }

    // 更新违约原因
    @PutMapping("/{id}")
    public ResponseEntity<BreachReasonResponseDTO> updateBreachReason(
            @PathVariable Long id,
            @Valid @RequestBody BreachReasonDTO breachReasonDTO) {
        BreachReasonResponseDTO updatedReason = breachReasonService.updateBreachReason(id, breachReasonDTO);
        return ResponseEntity.ok(updatedReason);
    }

    // 删除违约原因
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBreachReason(@PathVariable Long id) {
        breachReasonService.deleteBreachReason(id);
        return ResponseEntity.noContent().build();
    }

    // 启用/禁用违约原因
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleBreachReasonStatus(@PathVariable Long id) {
        breachReasonService.toggleBreachReasonStatus(id);
        return ResponseEntity.noContent().build();
    }
}