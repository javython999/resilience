package com.errday.mailservice.mail;

import com.errday.mailservice.exception.SmtpConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class SMTPClient {

    private final Random random = new Random();

    public void sendEmail(String email) {

        if (random.nextDouble() < 0.3) {
            log.error("SMTP 연결 실패: 서버에 접속할 수 없습니다.");
            throw new SmtpConnectionException("SMTP 연결 실패: 서버에 접속할 수 없습니다.");
        }

        log.info("{}로 메일 전송 성공", email);
    }
}
