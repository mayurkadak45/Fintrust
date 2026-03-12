package com.fintrust.customer.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBalanceRequest {
    private BigDecimal amount;
    private String type; // CREDIT or DEBIT
}
