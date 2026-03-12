package com.fintrust.customer.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDto {
    private String accountNo;
    private BigDecimal balance;
}
