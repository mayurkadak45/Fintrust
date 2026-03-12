package com.fintrust.atm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    @NotBlank(message = "Session token is required")
    private String sessionToken;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "100.00", message = "Minimum withdrawal is 100.00")
    private BigDecimal amount;
}
