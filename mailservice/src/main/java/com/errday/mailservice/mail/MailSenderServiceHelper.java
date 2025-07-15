package com.errday.mailservice.mail;

import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceHelper {

    private final MailSenderService mailSenderService;

    public MailSenderServiceHelper(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    // MailSenderService 내부적으로 sendEmail 호출
    public void sendEmailForAopDemo(String email) {
        // 내부적으로 호출된 경우 sendEmail에 @Retryable가 달려있는데도 불구하고 적용되지 않음
        mailSenderService.sendEmail(email);
    }
}