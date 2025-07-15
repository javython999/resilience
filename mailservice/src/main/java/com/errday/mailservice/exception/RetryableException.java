package com.errday.mailservice.exception;

public class RetryableException extends CustomUncheckedException {

    public RetryableException(String message) {
        super(message);
    }
}
