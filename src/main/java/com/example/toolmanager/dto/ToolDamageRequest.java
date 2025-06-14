package com.example.toolmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ToolDamageRequest {

    @NotBlank
    private String reason;
}
