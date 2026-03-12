package com.fintrust.atm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PinValidationRequest {
    @NotBlank(message = "Session token is required")
    private String sessionToken;

    @NotBlank(message = "PIN is required")
    private String pin;
}
