package com.errday.paymentservice.payment.model;

import lombok.Getter;

@Getter
public class PGResponse {

    private final String status;
    private final String message;

    public PGResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
