package com.fintrust.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtmTransactionRequest {
    private String accountNo;
    private BigDecimal amount;
    private String type; // WITHDRAWAL or DEPOSIT
}
