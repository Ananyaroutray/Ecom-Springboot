package com.java.ecom.dto.request;

import com.java.ecom.enums.PaymentMode;
import lombok.Data;

@Data
public class PaymentRequestDto {
    private PaymentMode paymentMode;
    private Boolean success; // true = payment success, false = failed
}
