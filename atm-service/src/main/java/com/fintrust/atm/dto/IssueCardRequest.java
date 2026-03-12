package com.fintrust.atm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IssueCardRequest {
    @NotBlank
    private String accountNo;
}

