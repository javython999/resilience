package com.errday.mainservice;

import com.errday.mainservice.mail.MailSendException;
import com.errday.mainservice.payment.PaymentServiceErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<String> handleMainSendException(MailSendException exception) {
        log.error("메일 전송 실패", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("메일 전송 실패: " + exception.getMessage());
    }

    @ExceptionHandler(PaymentServiceErrorException.class)
    public ResponseEntity<String> handlePaymentServiceErrorException(PaymentServiceErrorException ex) {
        log.error("결제 서비스 오류 발생", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("결제 서비스 오류: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.error("예기치 못한 오류 발생", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("예기치 못한 오류가 발생하였습니다.");
    }
}
