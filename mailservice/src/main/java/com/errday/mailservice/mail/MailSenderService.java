package com.errday.mailservice.mail;

import com.errday.mailservice.exception.InvalidEmailException;
import com.errday.mailservice.exception.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final SMTPClient smtpClient;
    private final List<String> sentEmails = new CopyOnWriteArrayList<>();

    @Retryable(
            retryFor = {RetryableException.class}, // 재시도할 예외 유형
            maxAttempts = 3,    // 최대 재시도 횟수
            backoff = @Backoff(delay = 2000),
            //backoff = @Backoff(delay = 1000, multiplier = 2) // 첫 재시도: 1초, 이후 2배씩 증가
            listeners = {"loggingRetryListener"} // 재시도에 대한 Listener 지정
    )
    public void sendEmail(String email) {

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InvalidEmailException("유효하지 않은 이메일 주소입니다. " + email);
        }

        smtpClient.sendEmail(email);

        this.sentEmails.add(email);
        log.info("성공 목록에 추가됨: {}, 현재 목록 크기: {}", email, sentEmails.size());
    }

    // 재시도를 모두 실패했을 때 실행되는 복구 메서드
    @Recover
    public void recover(RetryableException exception, String email) {
        log.error("모든 재시도가 실패했습니다. 이메일: {}에 대해 예외 발생: {}", exception.getMessage(), email);

        throw exception;
    }

    @Recover
    public void recover(RuntimeException exception, String email) {
        throw exception;
    }

    public List<String> getSentEmails() {
        return List.copyOf(sentEmails);
    }

}
