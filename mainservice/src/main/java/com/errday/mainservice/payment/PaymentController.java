package com.errday.mainservice.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /*@PostMapping("/process-webflux")
    public Mono<PaymentResponse> processPayment(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.processPaymentFlux(paymentRequest);
    }*/

    @PostMapping("/process-mvc")
    public PaymentResponse processPaymentMvc(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.processPaymentMvc(paymentRequest);
    }

}
