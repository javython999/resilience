package com.errday.mainservice.circuitbreaker;

public class MailServiceTimeoutException extends RecordException {

    public MailServiceTimeoutException(String message) {
        super(message);
    }

    public MailServiceTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
