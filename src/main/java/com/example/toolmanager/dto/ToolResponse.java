// 📤 ToolResponseブロック：クライアント返却用DTO（API応答）

package com.example.toolmanager.dto;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ToolResponse {
    
    private String status;
    private String username;
    private Double currentLat;
    private Double currentLng;
    private Double initialLat;
    private Double initialLng;





    private Long id;
    private String name;
    private String tagId;
   
    private Double lat;
    private Double lng;
    private boolean isDeleted;
}
