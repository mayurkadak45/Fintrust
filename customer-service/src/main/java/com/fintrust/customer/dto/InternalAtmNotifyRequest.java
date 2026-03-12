package com.fintrust.customer.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InternalAtmNotifyRequest {
    private String accountNo;
    private String type; // WITHDRAWAL or DEPOSIT
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private Long txnId;
    private LocalDateTime timestamp;
}

