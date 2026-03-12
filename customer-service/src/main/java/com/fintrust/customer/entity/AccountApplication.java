package com.fintrust.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String email;

    private String aadhaar;

    private String pan;

    private String aadhaarPhotoUrl;

    private String panPhotoUrl;

    private String passportPhotoUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(length = 500)
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
