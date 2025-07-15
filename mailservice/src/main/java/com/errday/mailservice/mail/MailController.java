package com.errday.mailservice.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailSenderService mailSenderService;
    private final MailSenderServiceHelper mailSenderServiceHelper;

    @PostMapping("/send")
    public String sendMail(@RequestBody EmailRequest emailRequest) {
        mailSenderService.sendEmail(emailRequest.getEmail());
        return "메일 전송 요청이 접수되었습니다.";
    }

    @PostMapping("/send-for-app-demo")
    public String sendMailAopDemo(@RequestBody EmailRequest emailRequest) {
        mailSenderServiceHelper.sendEmailForAopDemo(emailRequest.getEmail());
        return "메일 전송 요청이 접수되었습니다.";
    }

}
