package com.fintrust.transaction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fund_transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transferId;

    @Column(nullable = false)
    private String srcAccount;

    @Column(nullable = false)
    private String destAccount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    private String remarks;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.SUCCESS;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
