package com.fintrust.customer.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountSummaryDto {
    private String accountNo;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private String customerName;
}
