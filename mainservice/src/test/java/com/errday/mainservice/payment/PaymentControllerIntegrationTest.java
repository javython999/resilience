package com.errday.mainservice.payment;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.ResourceAccessException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@TestPropertySource(properties = {
        "server.tomcat.max-threads=10",
        "server.tomcat.accept-count=5"
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final int TEST_DURATION_SECONDS = 20;
    private static final int TARGET_RPS = 10;
    private static final int TOTAL_REQUESTS = TEST_DURATION_SECONDS * TARGET_RPS;

    /**
     * 60초 동안 /payments/process-mvc 엔드포인트에 초당 20개의 고정 부하를 가하는 테스트.
     * 총 1200개의 요청을 보냅니다.
     */
    @Test
    void testProcessMvcWithConstantLoad() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger requestCounter = new AtomicInteger(0);
        AtomicInteger totalSubmittedRequests = new AtomicInteger(0);

        log.info("고정 부하 테스트 시작: {}초 동안 {}개 요청 (총 {}개 예상)", TEST_DURATION_SECONDS, TARGET_RPS, TOTAL_REQUESTS);

        long testStartTime = System.currentTimeMillis();

        for (int t = 0; t < TEST_DURATION_SECONDS; t++) {
            long loopStartTime = System.currentTimeMillis();
            totalSubmittedRequests.addAndGet(TARGET_RPS); // 이번 초에 제출할 요청 수만큼 총계 업데이트

            log.info("Second {}: Submitting {} requests...", t + 1, TARGET_RPS);

            for (int i = 0; i < TARGET_RPS; i++) {
                int currentRequestNum = requestCounter.incrementAndGet(); // 1부터 시작하는 고유 번호

                executor.submit(() -> {
                    String orderId = "order-" + currentRequestNum;
                    String email = "user" + currentRequestNum + "@test.com";

                    try {
                        PaymentRequest request = new PaymentRequest();
                        request.setOrderId(orderId);
                        request.setEmail(email);
                        request.setAmount(100.0 + (currentRequestNum % 10));

                        ResponseEntity<String> response = restTemplate.postForEntity("/payments/process-mvc", request, String.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            failureCount.incrementAndGet();
                            log.warn("요청 실패: orderId = {}, email = {}, statusCode = {}", orderId, email, response.getStatusCode());
                        }
                    } catch (ResourceAccessException rae) {
                        failureCount.incrementAndGet();
                        log.error("네트워크 오류 발생: orderId = {}, email = {}, error = {}", orderId, email, rae.getMessage());
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        log.error("예기치 않은 오류 발생: orderId = {}, email = {}, error = {}", orderId, email, e.getMessage(), e);
                    }
                });
            }

            long loopEndTime = System.currentTimeMillis();
            long loopDuration = loopEndTime - loopStartTime;
            long sleepTime = 1000 - loopDuration; // 1초 = 1000ms

            if (sleepTime > 0) {
                Thread.sleep(sleepTime);
            } else {
                // 루프 실행 시간이 1초를 초과하면 다음 초에 더 많은 요청이 몰릴 수 있음
                log.warn("Second {}: Loop execution and submission took longer than 1 second ({}ms).", t + 1, loopDuration);
            }
        }

        log.info("모든 요청 작업 제출 완료 (총 {}개). 스레드 풀 종료 시작...", totalSubmittedRequests.get());

        // 스레드 풀 종료 (새 작업은 받지 않고 기존 작업 완료 대기)
        executor.shutdown();
        try {
            // 모든 작업이 완료될 때까지 대기 (예: 최대 2분 추가 대기)
            log.info("Executor 작업 완료 대기 중...");
            if (!executor.awaitTermination(120, TimeUnit.SECONDS)) { // 충분한 대기 시간 부여
                log.warn("스레드 풀이 시간 내에 완전히 종료되지 않았습니다. 강제 종료 시도.");
                executor.shutdownNow();
                // 강제 종료 후에도 대기 시간이 필요할 수 있음
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("스레드 풀 강제 종료 실패.");
                }
            } else {
                log.info("Executor 작업 모두 완료.");
            }
        } catch (InterruptedException ie) {
            log.warn("Executor 종료 대기 중 인터럽트 발생. 강제 종료 시도.");
            executor.shutdownNow();
            Thread.currentThread().interrupt(); // 인터럽트 상태 유지
        }

        long testEndTime = System.currentTimeMillis();
        long totalDurationMillis = testEndTime - testStartTime;

        // 최종 결과 로깅
        log.info("===== MVC 엔드포인트 고정 부하 테스트 결과 =====");
        log.info("총 테스트 시간: {}ms (약 {}초)", totalDurationMillis, totalDurationMillis / 1000);
        log.info("목표 요청 수: {} ({}초 * {} RPS)", TOTAL_REQUESTS, TEST_DURATION_SECONDS, TARGET_RPS);
        log.info("실제 제출된 요청 수: {}", totalSubmittedRequests.get());
        log.info("  - 성공 요청 수: {}", successCount.get());
        log.info("  - 실패 요청 수: {}", failureCount.get());
        log.info("==========================================");

    }

}
