package com.errday.mailservice.exception;

public class CustomUncheckedException extends RuntimeException {

    public CustomUncheckedException(String message) {
        super(message);
    }
}
