package com.fintrust.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiaryDto {
    private Long id;

    @NotBlank(message = "Account number is required")
    private String accountNo;

    private String ifsc;

    @NotBlank(message = "Beneficiary name is required")
    private String beneficiaryName;

    private String status;
}
