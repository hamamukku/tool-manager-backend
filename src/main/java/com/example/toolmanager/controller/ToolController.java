package com.example.toolmanager.controller;

import com.example.toolmanager.dto.*;
import com.example.toolmanager.service.ToolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class ToolController {

    private final ToolService toolService;

    // 工具一覧取得
    @GetMapping
    public List<ToolResponse> getAllTools() {
        return toolService.getAllTools();
    }

    // 工具新規登録
    @PostMapping
    public ResponseEntity<ToolResponse> createTool(@RequestBody @Valid ToolRequest request) {
        return ResponseEntity.ok(toolService.createTool(request));
    }

    // 工具情報更新
    @PutMapping("/{id}")
    public ResponseEntity<ToolResponse> updateTool(@PathVariable Long id, @RequestBody @Valid ToolRequest request) {
        return ResponseEntity.ok(toolService.updateTool(id, request));
    }

    // 工具削除（論理削除）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTool(@PathVariable Long id) {
        toolService.deleteTool(id);
        return ResponseEntity.noContent().build();
    }

    // 破損報告の登録
    @PostMapping("/{id}/damage")
    public ResponseEntity<Void> reportDamage(@PathVariable Long id, @RequestBody @Valid ToolDamageRequest request) {
        toolService.reportDamage(id, request);
        return ResponseEntity.ok().build();
    }

    // 破損レポート取得
    @GetMapping("/{id}/damage-report")
    public ResponseEntity<String> getDamageReport(@PathVariable Long id) {
        String reportText = toolService.getDamageReport(id);
        return ResponseEntity.ok(reportText);
    }

    // 座標逸脱通知
    @GetMapping("/notify/location-alerts")
    public ResponseEntity<Void> notifyOutOfBoundsTools() {
        toolService.notifyOutOfBoundsTools();
        return ResponseEntity.ok().build();
    }

    // 返却超過通知
    @GetMapping("/notify/overdue-check")
    public ResponseEntity<Void> notifyOverdueTools() {
        toolService.notifyOverdueTools();
        return ResponseEntity.ok().build();
    }
}
