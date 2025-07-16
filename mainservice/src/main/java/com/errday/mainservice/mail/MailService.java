package com.errday.mainservice.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final MailServiceClient mailServiceClient;

    /*public Mono<String> sendMail(String email) {
        log.info("메일 발송 요청: {}", email);
        return mailServiceClient.sendMail(email)
                .doOnSuccess(response -> log.info("메일 발송 성공: {}", response))
                .doOnError(error -> log.error("메일 발송 실패: {}", email, error))
                .onErrorMap(ex -> {
                    if (!(ex instanceof MailSendException)) {
                        return new MailSendException("메일 전송 중 오류 발생: " + email, ex);
                    }
                    return ex;
                });
    }*/

    public String sendMail(String email) {
        log.info("메일 발송 요청: {}", email);
        return mailServiceClient.sendMail(email);
    }
}
