package com.fintrust.atm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePinRequest {

    @NotBlank
    @Pattern(regexp = "^\\d{12}$", message = "Card number must be 12 digits")
    private String cardNumber;

    @NotBlank
    private String accountNo;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Pattern(regexp = "^\\d{4}$", message = "PIN must be 4 digits")
    private String newPin;
}

