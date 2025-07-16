package com.errday.mainservice.payment;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PaymentServiceClient {

    private final WebClient webClient;

    public PaymentServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    public Mono<PaymentResponse> processPaymentFlux(PaymentRequest paymentRequest) {
        return webClient.post()
                .uri("/payments/process")
                .bodyValue(paymentRequest)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new PaymentServiceErrorException("결제 서비스 에러: " + errorBody))))
                .bodyToMono(PaymentResponse.class);
    }

    public PaymentResponse processPaymentMvc(PaymentRequest paymentRequest) {
        return webClient.post()
                .uri("/payments/process")
                .bodyValue(paymentRequest)  // PaymentRequest를 JSON 바디로 전달
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody ->
                                        Mono.error(new PaymentServiceErrorException("결제 서비스 에러: " + errorBody))
                                )
                )
                .bodyToMono(PaymentResponse.class)
//                .timeout(Duration.ofSeconds(15)) // 필요에 따라 Timeout 설정 가능
                .block(); // 블로킹 호출로 MVC 방식 구현
    }
}
