package com.fintrust.atm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardValidationRequest {
    @NotBlank(message = "Card number is required")
    private String cardNumber;
}
