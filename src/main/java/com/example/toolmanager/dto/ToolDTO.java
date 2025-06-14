// 🧩 ToolDTOブロック：Service内部用の中間データ変換DTO

package com.example.toolmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolDTO {
    private Long id;
    private String name;
    private String tagId;
    private String status;
    private Long userId;
    private Double initialLat;
    private Double initialLng;
    private Double currentLat;
    private Double currentLng;
}
