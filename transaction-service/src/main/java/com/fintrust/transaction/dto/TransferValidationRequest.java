package com.fintrust.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferValidationRequest {
    @NotBlank(message = "Source account is required")
    private String srcAccount;

    @NotBlank(message = "Destination account is required")
    private String destAccount;

    private BigDecimal amount;
}
