package com.errday.mailservice.exception;

public class SmtpConnectionException extends RetryableException {

    public SmtpConnectionException(String message) {
        super(message);
    }
}
