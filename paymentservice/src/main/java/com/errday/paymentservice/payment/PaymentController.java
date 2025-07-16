package com.errday.paymentservice.payment;

import com.errday.paymentservice.payment.model.PGResponse;
import com.errday.paymentservice.payment.model.PaymentRequest;
import com.errday.paymentservice.payment.model.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(paymentResponse);
    }

    @PostMapping("/mock-pg")
    public ResponseEntity<?> mockPG(@RequestBody PaymentRequest paymentRequest) throws InterruptedException {
        double chance = ThreadLocalRandom.current().nextDouble();

        if (chance < 1.0) {
            Thread.sleep(2000);
            PGResponse response = new PGResponse("SUCCESS", "Payment processed successfully");
            return ResponseEntity.ok(response);
        } else {
            Thread.sleep(6000);
            PGResponse response = new PGResponse("SUCCESS", "Payment processed successfully after delay");
            return ResponseEntity.ok(response);
        }
    }
}
