package com.fintrust.atm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @Column(unique = true, nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String accountNo;

    @Column(nullable = false)
    private String pinHash;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CardStatus status = CardStatus.ACTIVE;

    @Builder.Default
    private BigDecimal dailyLimit = new BigDecimal("50000.00");

    @Builder.Default
    private BigDecimal dailyWithdrawn = BigDecimal.ZERO;

    @Builder.Default
    private int incorrectAttempts = 0;
}
