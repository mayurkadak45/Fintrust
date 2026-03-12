package com.fintrust.customer.dto;

import lombok.Data;

@Data
public class InternalPinChangedRequest {
    private String accountNo;
    private String cardMasked;
}

