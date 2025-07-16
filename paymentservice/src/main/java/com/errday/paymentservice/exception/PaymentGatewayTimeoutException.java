package com.errday.paymentservice.exception;

public class PaymentGatewayTimeoutException extends RetryableException {

    public PaymentGatewayTimeoutException(String message) {
        super(message);
    }

    public PaymentGatewayTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
