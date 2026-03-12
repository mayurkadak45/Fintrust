package com.fintrust.atm.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AtmSessionDto implements Serializable {
    private String sessionToken;
    private String cardNumber;
    private String accountNo;
    private boolean pinVerified;
}
