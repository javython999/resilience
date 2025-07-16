package com.errday.paymentservice.payment.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

    private String orderId;
    private Double amount;
}
