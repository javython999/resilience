package com.errday.paymentservice.payment;

import com.errday.paymentservice.exception.PaymentGatewayServerErrorException;
import com.errday.paymentservice.exception.PaymentGatewayTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentGatewayTimeoutException.class)
    public ResponseEntity<String> handlePaymentGatewayTimeoutException(PaymentGatewayTimeoutException exception) {
        log.error("Timeout Exception while calling PG service", exception);
        return new ResponseEntity<>("PG 요청 타임아웃 발생", HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(PaymentGatewayServerErrorException.class)
    public ResponseEntity<String> handlePaymentGatewayServerErrorException(PaymentGatewayServerErrorException exception) {
        log.error("Server Error Exception while calling PG service", exception);
        return new ResponseEntity<>("PG 서버 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception exception) {
        log.error("Unhandled exception occurred", exception);
        return new ResponseEntity<>("PG 서비스 처리 중 요류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
