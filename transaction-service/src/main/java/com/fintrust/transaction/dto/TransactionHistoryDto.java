package com.fintrust.transaction.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryDto {
    private Long txnId;
    private String accountNo;
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String status;
    private LocalDateTime timestamp;
    private String description;
}
