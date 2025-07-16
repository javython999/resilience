package com.errday.paymentservice.payment.model;

import lombok.Getter;

@Getter
public class PaymentResponse {

    private final String status;
    private final String message;

    private PaymentResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static PaymentResponse of(String status, String message) {
        return new PaymentResponse(status, message);
    }

}
