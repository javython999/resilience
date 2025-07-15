package com.errday.mailservice.mail;

import lombok.Getter;

@Getter
public class EmailRequest {

    private final String email;

    public EmailRequest(String email) {
        this.email = email;
    }
}
