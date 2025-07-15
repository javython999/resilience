package com.errday.mainservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final MailServiceClient mailServiceClient;

    public String sendEmail() {
        try {
            return mailServiceClient.sendMail();
        } catch (Exception exception) {
            throw new MailSendException("메일 전송 중 요류 발생", exception);
        }
    }
}
