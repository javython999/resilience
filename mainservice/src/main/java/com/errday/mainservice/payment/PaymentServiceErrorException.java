package com.errday.mainservice.payment;

public class PaymentServiceErrorException extends RuntimeException {

    public PaymentServiceErrorException(String message) {
        super(message);
    }

    public PaymentServiceErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
