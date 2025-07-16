package com.errday.paymentservice.payment;

import com.errday.paymentservice.payment.model.PGRequest;
import com.errday.paymentservice.payment.model.PGResponse;
import com.errday.paymentservice.payment.model.PaymentRequest;
import com.errday.paymentservice.payment.model.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final SomePGClient pgClient;

    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        PGRequest pgRequest = PGRequest.of(paymentRequest.getOrderId(),  paymentRequest.getAmount());

        PGResponse pgResponse = pgClient.callPGService(pgRequest);

        return PaymentResponse.of(pgResponse.getStatus(), pgResponse.getMessage());
    }
}
