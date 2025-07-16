package com.errday.paymentservice.exception;

public class PaymentGatewayServerErrorException extends RetryableException {

    public PaymentGatewayServerErrorException(String message) {
        super(message);
    }

    public PaymentGatewayServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
