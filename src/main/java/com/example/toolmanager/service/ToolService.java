package com.example.toolmanager.service;

import com.example.toolmanager.dto.*;
import com.example.toolmanager.entity.ToolEntity;
import com.example.toolmanager.entity.UserEntity;
import com.example.toolmanager.repository.ToolRepository;
import com.example.toolmanager.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ToolService {

    private final ToolRepository toolRepository;
    private final MailService mailService; // ← メール通知対応

    public List<ToolResponse> getAllTools() {
        return toolRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ToolResponse createTool(ToolRequest request) {
        ToolEntity tool = ToolEntity.builder()
                .name(request.getName())
                .tagId(request.getTagId())
                .status(request.getStatus() != null ? request.getStatus() : "available")
                .initialLat(request.getInitialLat())
                .initialLng(request.getInitialLng())
                .currentLat(request.getInitialLat())
                .currentLng(request.getInitialLng())
                .deleted(false)
                .build();
        return toResponse(toolRepository.save(tool));
    }

    public ToolResponse updateTool(Long id, ToolRequest request) {
        ToolEntity tool = toolRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Tool not found"));
        tool.setName(request.getName());
        tool.setTagId(request.getTagId());
        tool.setStatus(request.getStatus());
        tool.setInitialLat(request.getInitialLat());
        tool.setInitialLng(request.getInitialLng());
        return toResponse(toolRepository.save(tool));
    }

    public void deleteTool(Long id) {
        ToolEntity tool = toolRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Tool not found"));
        tool.setDeleted(true);
        toolRepository.save(tool);
    }

    public ToolResponse checkoutTool(Long id, toolCheckoutrequest request) {
        ToolEntity tool = toolRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Tool not found"));

        if (tool.getUser() != null) {
            throw new RuntimeException("Tool is already checked out");
        }

        UserEntity user = new UserEntity();
        user.setId(request.getUserId());
        tool.setUser(user);
        tool.setBorrowedAt(LocalDateTime.now());
        tool.setReturnAt(request.getReturnAt());
        tool.setStatus("borrowed");

        return toResponse(toolRepository.save(tool));
    }

    public ToolResponse updateLocation(Long id, ToolLocationRequest request) {
        ToolEntity tool = toolRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Tool not found"));

        tool.setCurrentLat(request.getLat());
        tool.setCurrentLng(request.getLng());

        return toResponse(toolRepository.save(tool));
    }

    public boolean isToolOutOfBounds(Long id) {
        ToolEntity tool = toolRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Tool not found"));

        Double lat1 = tool.getInitialLat();
        Double lng1 = tool.getInitialLng();
        Double lat2 = tool.getCurrentLat();
        Double lng2 = tool.getCurrentLng();

        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return false;
        }

        double distance = GeoUtils.calculateDistanceKm(lat1, lng1, lat2, lng2);
        return distance > 2.0;
    }

    public void reportDamage(Long id, ToolDamageRequest request) {
        ToolEntity tool = toolRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Tool not found"));

        String fileName = String.format("damage-reports/tool_%03d_damage_%s.txt",
                tool.getId(),
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));

        String content = String.format(
                "【工具破損報告書】\n\n" +
                "工具名：%s\n破損日時：%s\n報告者：%s\n\n理由：\n%s\n",
                tool.getName(),
                LocalDateTime.now(),
                tool.getUser() != null ? tool.getUser().getUsername() : "不明",
                request.getReason()
        );

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save damage report", e);
        }

        tool.setStatus("damaged");
        toolRepository.save(tool);
    }

    public String getDamageReport(Long id) {
        ToolEntity tool = toolRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Tool not found"));

        String fileName = String.format("damage-reports/tool_%03d_damage_%s.txt",
                tool.getId(),
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Report not found for tool ID: " + id);
        }
    }

    public void notifyOutOfBoundsTools() {
        List<ToolEntity> tools = toolRepository.findAll();
        for (ToolEntity tool : tools) {
            if (tool.getUser() == null || tool.getUser().getEmail() == null) continue;
            if (tool.getInitialLat() == null || tool.getCurrentLat() == null) continue;

            double distance = GeoUtils.calculateDistanceKm(
                    tool.getInitialLat(), tool.getInitialLng(),
                    tool.getCurrentLat(), tool.getCurrentLng());

            if (distance > 2.0) {
                String email = tool.getUser().getEmail();
                String subject = "[警告] 工具の現在位置が2km以上離れています";
                String body = String.format("工具「%s」が逸脱しました。現在距離：%.2fkm", tool.getName(), distance);
                mailService.sendToUser(email, subject, body);
            }
        }
    }

    public void notifyOverdueTools() {
        List<ToolEntity> tools = toolRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (ToolEntity tool : tools) {
            if (tool.getUser() == null || tool.getUser().getEmail() == null) continue;
            if (tool.getReturnAt() == null || !"borrowed".equals(tool.getStatus())) continue;

            LocalDateTime deadline = tool.getReturnAt().plusHours(3);
            if (now.isAfter(deadline)) {
                String email = tool.getUser().getEmail();
                String subject = "[警告] 工具の返却期限を超過しています";
                String body = String.format("工具「%s」の返却期限が過ぎています。\n返却予定：%s\n現在時刻：%s",
                        tool.getName(), tool.getReturnAt(), now);

                mailService.sendToUser(email, subject, body);
            }
        }
    }

    private ToolResponse toResponse(ToolEntity tool) {
        return ToolResponse.builder()
                .id(tool.getId())
                .name(tool.getName())
                .tagId(tool.getTagId())
                .status(tool.getStatus())
                .username(tool.getUser() != null ? tool.getUser().getUsername() : null)
                .initialLat(tool.getInitialLat())
                .initialLng(tool.getInitialLng())
                .currentLat(tool.getCurrentLat())
                .currentLng(tool.getCurrentLng())
                .isDeleted(tool.isDeleted())
                .build();
    }
}
