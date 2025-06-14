package com.example.toolmanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class toolCheckoutrequest {

    @NotNull
    private Long userId;

    @NotNull
    @Future(message = "返却予定日時は未来にしてください")
    private LocalDateTime returnAt;
}
