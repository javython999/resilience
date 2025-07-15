package com.errday.mailservice.exception;

public class SomeRetryableException extends RetryableException{

    public SomeRetryableException(String message) {
        super(message);
    }
}
