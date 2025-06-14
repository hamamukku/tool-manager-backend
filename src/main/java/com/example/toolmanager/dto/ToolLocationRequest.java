package com.example.toolmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ToolLocationRequest {

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;
}
