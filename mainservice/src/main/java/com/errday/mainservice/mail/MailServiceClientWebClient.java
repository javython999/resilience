package com.errday.mainservice.mail;

import com.errday.mainservice.circuitbreaker.ConnectionRefusedException;
import com.errday.mainservice.circuitbreaker.MailServiceTimeoutException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.time.Duration;

@Slf4j
@Component
public class MailServiceClientWebClient implements MailServiceClient {

    private final WebClient webClient;

    private static final String MAIL_SERVICE_RL = "mailServiceRateLimit";
    private static final String MAIL_SERVICE_BH = "mailServiceBulkhead";
    private static final String MAIL_SEND_URI = "/mail/send";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private static final int MAX_CAUSE_DEPTH = 10;

    public MailServiceClientWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }


    /*@Override
    public Mono<String> sendMail(String email) {
        EmailRequest request = new EmailRequest(email);

        return webClient.post()
                .uri("/mail/send")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MailSendException("서버 에러 발생: " + errorBody)))
                )
                .bodyToMono(String.class);
    }*/

    /*@Override
    //@RateLimiter(name = "MAIL_SERVICE_RL", fallbackMethod = "sendMailFallback")
    @Bulkhead(name = "MAIL_SERVICE_BH", fallbackMethod = "sendMailFallback")
    public String sendMail(String email) {
        log.debug("[Resilience] Attempting to send mail for email: {}", email);
        EmailRequest request = new EmailRequest(email);

        // WebClient 호출 및 결과 처리
        // .block()을 사용하므로, 전체 메서드는 동기적으로 동작합니다.
        // 완전한 비동기 처리를 원한다면 Mono<String>을 반환하도록 수정해야 합니다.
        return webClient.post()
                .uri(MAIL_SEND_URI) // 상수 사용
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.warn("[Resilience] Mail service returned 5xx server error for {}: {}", email, errorBody);
                                    return Mono.error(new MailSendException("Mail service server error: " + errorBody));
                                })
                )
                .bodyToMono(String.class)
                .timeout(DEFAULT_TIMEOUT, Mono.error(
                        () -> new MailServiceTimeoutException("Timeout after " + DEFAULT_TIMEOUT + " for " + email))
                )
                .doOnSuccess(response -> log.debug("[Resilience] Successfully sent mail for {}", email))
                .block();
    }*/

    @Override
    //@RateLimiter(name = "MAIL_SERVICE_RL", fallbackMethod = "sendMailFallback")
    @Bulkhead(name = "MAIL_SERVICE_BH", fallbackMethod = "sendMailFallback")
    public String sendMail(EmailRequest emailRequest) {
        log.debug("[Resilience] Attempting to send mail for email: {}", emailRequest.getEmail());
        EmailRequest request = new EmailRequest(emailRequest.getEmail());

        // WebClient 호출 및 결과 처리
        // .block()을 사용하므로, 전체 메서드는 동기적으로 동작합니다.
        // 완전한 비동기 처리를 원한다면 Mono<String>을 반환하도록 수정해야 합니다.
        return webClient.post()
                .uri(MAIL_SEND_URI) // 상수 사용
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.warn("[Resilience] Mail service returned 5xx server error for {}: {}", emailRequest.getEmail(), errorBody);
                                    return Mono.error(new MailSendException("Mail service server error: " + errorBody));
                                })
                )
                .bodyToMono(String.class)
                .timeout(DEFAULT_TIMEOUT, Mono.error(
                        () -> new MailServiceTimeoutException("Timeout after " + DEFAULT_TIMEOUT + " for " + emailRequest.getEmail()))
                )
                .doOnSuccess(response -> log.debug("[Resilience] Successfully sent mail for {}", emailRequest.getEmail()))
                .block();
    }


    /**
     * sendMail 메소드의 Fallback 메소드.
     * Circuit Breaker가 Open 상태이거나, recordExceptions에 지정된 예외가 발생했을 때 호출됩니다.
     */
    public String sendMailFallback(String email, Throwable t) {
        // 어떤 예외로 Fallback이 실행되었는지 로깅 (RateLimiter, Bulkhead 예외 포함)
        log.warn("[Resilience Fallback] Fallback activated for email: {}. Reason: {}",
                email, t.getClass().getSimpleName() + ": " + t.getMessage());

        // Fallback 로직
        // 유저에 의한 호출이었다면, 추상적인 예외를 만들어서 클라이언트에게 횟수 제한을 초과했다던지 알려주면 좋음.
        throw new RuntimeException("Mail service temporarily unavailable or limit exceeded. Fallback executed for " + email + ". Cause: " + t.getMessage(), t);
    }

}
