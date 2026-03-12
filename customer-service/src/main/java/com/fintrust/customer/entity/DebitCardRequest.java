package com.fintrust.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "debit_card_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitCardRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountNo;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DebitCardRequestStatus status = DebitCardRequestStatus.PENDING;

    private String approvedCardNumber;

    private String approvedExpiryDate; // ISO-8601 date string

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}

