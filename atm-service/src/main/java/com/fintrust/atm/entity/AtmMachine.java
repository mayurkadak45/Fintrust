package com.fintrust.atm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "atm_machines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtmMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long atmId;

    private String location;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal cashAvailable = new BigDecimal("500000.00");

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AtmStatus status = AtmStatus.ACTIVE;
}
