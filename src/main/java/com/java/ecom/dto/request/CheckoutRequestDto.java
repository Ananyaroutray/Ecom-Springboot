package com.java.ecom.dto.request;

import com.java.ecom.enums.PaymentMode;
import lombok.Data;

@Data
public class CheckoutRequestDto {
    private Long addressId; // user’s selected address
    private PaymentMode paymentMode; // ONLINE or CASH_ON_DELIVERY
}
