package com.errday.mainservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    @GetMapping("/send")
    public ResponseEntity<String> sendMail() {
        try {
            String response = mailService.sendEmail();
            return ResponseEntity.ok(response);
        } catch (MailSendException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("메일 전송 실패: " + exception.getMessage());
        }
    }
}
