package com.errday.mailservice.mail;

import com.errday.mailservice.exception.CustomUncheckedException;
import com.errday.mailservice.exception.InvalidEmailException;
import com.errday.mailservice.exception.SmtpConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SmtpConnectionException.class)
    public ResponseEntity<String> handleSmtpConnectionException(SmtpConnectionException exception) {
        log.error("SMTP 연결 오류 발생", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("SMTP 연결 오류: " + exception.getMessage());
    }

    @ExceptionHandler(CustomUncheckedException.class)
    public ResponseEntity<String> handleCustomUncheckedException(CustomUncheckedException exception) {
        log.error("메일 전송 중 오류 발생", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("메일 전송 중 오류 발생: " + exception.getMessage());
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<String> handleInvalidEmailException(InvalidEmailException exception) {
        log.error("유효하지 않은 이메일 주소 예외 발생", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("유효하지 않은 이메일 주소: " + exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception exception) {
        log.error("예기치 못한 오류 발생", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("예기치 못한 오류가 발생하였습니다.");
    }
}
