package com.fintrust.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCustomerUserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    private String email;
}

