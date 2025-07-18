package com.errday.mainservice.payment;

import com.errday.mainservice.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentServiceClient paymentServiceClient;
    private final MailService mailService;

    /*public Mono<PaymentResponse> processPaymentFlux(PaymentRequest paymentRequest) {
        return paymentServiceClient.processPaymentFlux(paymentRequest)
                .flatMap(paymentResponse -> {
                    log.info("결제 승인 완료: 주문ID = {}, 결제 상태 = {}", paymentRequest.getOrderId(), paymentResponse.getStatus());

                    log.info("결재 내역 DB 갱신 요청: 주문ID = {}, 금액 = {}",  paymentRequest.getOrderId(), paymentRequest.getAmount());

                    return mailService.sendMail("test@test.com")
                            .doOnSuccess(mailResponse -> log.info("메일 발송 완료: {}", mailResponse))
                            .thenReturn(paymentResponse);
                });
    }*/

    public PaymentResponse processPaymentMvc(PaymentRequest request) {
        // 1. 결제 요청 (블로킹)
        PaymentResponse paymentResponse = paymentServiceClient.processPaymentMvc(request);
//        logger.info("결제 승인 완료: 주문ID={}, 결제상태={}", request.getOrderId(), paymentResponse.getStatus());

        // 2. 결제 DB 갱신 (TODO)
//        logger.info("결제 내역 DB 갱신 요청: 주문ID={}, 금액={}", request.getOrderId(), request.getAmount());
        // 만약 DB 갱신도 별도의 MVC 방식으로 처리한다면 해당 로직 추가

        // 3. 메일 발송 (블로킹)
        // MailService의 sendMail 메서드가 리액티브 Mono<String>을 반환하는 경우 block()을 이용하여 동기적으로 호출
        String mailResponse = mailService.sendMail(request.getEmail(), paymentResponse);
        log.info("메일 발송 완료: {}", mailResponse);

        return paymentResponse;
    }
}
