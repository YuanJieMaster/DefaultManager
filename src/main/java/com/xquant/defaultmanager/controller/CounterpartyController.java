package com.xquant.defaultmanager.controller;

import com.xquant.defaultmanager.entity.Counterparty;
import com.xquant.defaultmanager.service.CounterpartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/counterparties")
@RequiredArgsConstructor
@Tag(name = "交易对手管理", description = "交易对手的增删改查接口")
public class CounterpartyController {

    @Resource
    private CounterpartyService counterpartyService;

    @PostMapping
    @Operation(summary = "创建交易对手")
    public ResponseEntity<Counterparty> createCounterparty(@Valid @RequestBody Counterparty counterparty) {
        Counterparty created = counterpartyService.createCounterparty(counterparty);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新交易对手")
    public ResponseEntity<Counterparty> updateCounterparty(
            @PathVariable Long id,
            @Valid @RequestBody Counterparty counterparty) {
        Counterparty updated = counterpartyService.updateCounterparty(id, counterparty);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除交易对手")
    public ResponseEntity<Void> deleteCounterparty(@PathVariable Long id) {
        counterpartyService.deleteCounterparty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取交易对手")
    public ResponseEntity<Counterparty> getCounterpartyById(@PathVariable Long id) {
        Optional<Counterparty> counterparty = counterpartyService.getCounterpartyById(id);
        return counterparty.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "根据代码获取交易对手")
    public ResponseEntity<Counterparty> getCounterpartyByCode(@PathVariable String code) {
        Optional<Counterparty> counterparty = counterpartyService.getCounterpartyByCode(code);
        return counterparty.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "获取所有交易对手")
    public ResponseEntity<List<Counterparty>> getAllCounterparties() {
        List<Counterparty> counterparties = counterpartyService.getAllCounterparties();
        return ResponseEntity.ok(counterparties);
    }

    @GetMapping("/paged")
    @Operation(summary = "分页获取交易对手")
    public ResponseEntity<Page<Counterparty>> getCounterpartiesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Counterparty> counterparties = counterpartyService.getCounterparties(pageable);
        return ResponseEntity.ok(counterparties);
    }

    @GetMapping("/rating/{rating}")
    @Operation(summary = "根据信用评级获取交易对手")
    public ResponseEntity<List<Counterparty>> getCounterpartiesByRating(@PathVariable String rating) {
        List<Counterparty> counterparties = counterpartyService.getCounterpartiesByRating(rating);
        return ResponseEntity.ok(counterparties);
    }
}
