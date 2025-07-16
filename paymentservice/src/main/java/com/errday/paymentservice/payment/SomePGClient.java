package com.errday.paymentservice.payment;

import com.errday.paymentservice.exception.PaymentGatewayTimeoutException;
import com.errday.paymentservice.exception.RetryableException;
import com.errday.paymentservice.payment.model.PGRequest;
import com.errday.paymentservice.payment.model.PGResponse;
import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Component
public class SomePGClient {

    private final WebClient webClient;

    public SomePGClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("http://localhost:8082/payments")
                .build();
    }

    @Retryable(
            retryFor = {RetryableException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 300)
    )
    public PGResponse callPGService(PGRequest request) {
        return webClient.post()
                .uri("/mock-pg")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                /*.onStatus(status -> status.is5xxServerError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new PaymentGatewayServerErrorException(errorBody))))*/
                .bodyToMono(PGResponse.class)
                // 발생한 예외 또는 그 원인이 ReadTimeoutException인 경우 PaymentGatewayTimeoutException으로 매핑
                .onErrorMap(throwable -> {
                    if (throwable instanceof ReadTimeoutException
                            || (throwable.getCause() != null && throwable.getCause() instanceof ReadTimeoutException)) {
                        return new PaymentGatewayTimeoutException("PG 서비스 호출 시 타임아웃 발생", throwable);
                    }
                    return throwable;
                })
                .block();
    }
}
