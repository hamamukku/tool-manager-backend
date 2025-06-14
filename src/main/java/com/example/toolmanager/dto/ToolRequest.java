// 📥 DTOブロック：新規作成・更新時の受信用データ
package com.example.toolmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ToolRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String tagId;

    // 初期位置（登録時）
    private Double initialLat;
    private Double initialLng;

    // ステータス（任意入力。通常は"available"で初期化）
    private String status;
}
