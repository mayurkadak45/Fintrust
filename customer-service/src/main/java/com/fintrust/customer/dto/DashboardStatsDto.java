package com.fintrust.customer.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDto {
    private long totalCustomers;
    private long pendingAccounts;
    private long totalTransactions;
    private BigDecimal bankBalance;
}
