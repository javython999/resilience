package com.errday.mailservice.mail.aop;

import com.errday.mailservice.exception.InvalidEmailException;
import com.errday.mailservice.exception.RetryableException;
import com.errday.mailservice.mail.SMTPClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderServiceNoAop {

    private final SMTPClient smtpClient;

    private static final int MAX_ATTEMPTS = 3;
    private static final long DELAY_MILLIS = 2000;

    public void sendEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InvalidEmailException("유효하지 않은 이메일 주소입니다: " + email);
        }

        RetryableException lastException = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                smtpClient.sendEmail(email);
                return; // 성공 시 종료
            } catch (RetryableException e) {
                lastException = e;
                if (attempt < MAX_ATTEMPTS) {
                    try {
                        Thread.sleep(DELAY_MILLIS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("이메일 발송 재시도 중 인터럽트됨: " + email, ie);
                    }
                }
            } catch (Exception e) {
                // RetryableException이 아닌 다른 예외는 즉시 전파
                throw e;
            }
        }

        // 모든 재시도 실패 시 마지막 예외를 던짐
        if (lastException != null) {
            throw lastException;
        }
    }

}
