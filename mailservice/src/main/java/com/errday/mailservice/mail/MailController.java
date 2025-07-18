package com.errday.mailservice.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/list")
    public ResponseEntity<List<String>> getSentMailList() {
        List<String> sentEmails = mailSenderService.getSentEmails();
        return ResponseEntity.ok(sentEmails);
    }

}
