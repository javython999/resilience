package com.errday.mainservice.mail;

import reactor.core.publisher.Mono;

public interface MailServiceClient {

    //Mono<String> sendMail(String email);

    String sendMail(String email);
}
