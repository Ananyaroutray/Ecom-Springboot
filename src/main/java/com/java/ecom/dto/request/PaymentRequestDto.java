package com.java.ecom.dto.request;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private boolean success; // true = payment success, false = failed
}
