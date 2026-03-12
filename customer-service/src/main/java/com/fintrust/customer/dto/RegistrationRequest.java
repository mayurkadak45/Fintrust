package com.fintrust.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistrationRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile is required")
    private String mobile;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String aadhaar;

    private String pan;

    private String aadhaarPhotoUrl;

    private String panPhotoUrl;

    private String passportPhotoUrl;

    @NotNull(message = "Account type is required")
    private String accountType;

    private String address;
}
