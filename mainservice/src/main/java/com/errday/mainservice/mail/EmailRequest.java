package com.errday.mainservice.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    private String email;
    private String emailBody;

    public EmailRequest(String email) {
        this.email = email;
    }
}
