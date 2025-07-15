package com.errday.mailservice.exception;

public class InvalidEmailException extends CustomUncheckedException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
