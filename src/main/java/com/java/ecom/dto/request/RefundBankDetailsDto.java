package com.java.ecom.dto.request;

import lombok.Data;

@Data
public class RefundBankDetailsDto {
    private String upiId;
    private String bankAccount;
    private String ifscCode;
}

