package com.fintrust.customer.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailDto {
    private Long id;
    private String name;
    private String accountNo;
    private String email;
    private String mobile;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private String createdAt;
    /** Login username for the customer (set on approval, included in customer list). */
    private String username;
    /**
     * Temporary login password generated when the account is approved.
     * Will be null for existing customers.
     */
    private String temporaryPassword;
}
