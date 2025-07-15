package com.errday.mainservice;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//@Component
public class MailServiceClientRestTemplate implements MailServiceClient {

    private final RestTemplate restTemplate =  new RestTemplate();

    @Override
    public String sendMail() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/mail/send", String.class);

        return response.getBody();
    }
}
