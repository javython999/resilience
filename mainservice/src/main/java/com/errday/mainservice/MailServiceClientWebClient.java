package com.errday.mainservice;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class MailServiceClientWebClient implements MailServiceClient {

    private final WebClient webClient;

    public MailServiceClientWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    @Override
    public String sendMail() {
        return webClient.get()
                .uri("/mail/send")
                .retrieve()
                // 5xx 서버 에러 발생 시 예외 처리.
                .onStatus(status -> status.is5xxServerError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MailSendException("서버 에러 발생: " + errorBody)))
                )
                .bodyToMono(String.class)
                .block();
    }


}
