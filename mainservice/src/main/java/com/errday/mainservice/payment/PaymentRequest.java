package com.errday.mainservice.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

    private String orderId;
    private Double amount;
    private String email;

}
