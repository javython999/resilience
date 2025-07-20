package com.errday.orderservice.order;

import com.errday.orderservice.kafka.KafkaProducerService;
import com.errday.orderservice.outbox.OutboxEvent;
import com.errday.orderservice.outbox.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void placeOrder_Problematic(OrderRequestDto request) {
        orderRepository.save(new Order(request.productName(), request.quantity()));
        log.info("[DB] 주문 정보 저장 완료 (아직 커밋 전)");

        kafkaProducerService.send("stock-decrease-topic", request.toJson(objectMapper));
        log.info("[Kafka] 재고 감소 이벤트 발행 성공");

        log.error("[결제] 결제 실패 ! 예외 발생");
        throw new RuntimeException("결제 처리 중 장애가 발생했습니다.");
    }

    @Transactional
    public void placeOrder_Withoutbox(OrderRequestDto request) {
        orderRepository.save(new Order(request.productName(), request.quantity()));
        log.info("[DB] 주문 정보 저장 완료 (아직 커밋 전)");

        String payload = request.toJson(objectMapper);
        OutboxEvent outboxEvent = new OutboxEvent("STOCK_DECREASED", payload);
        outboxEventRepository.save(outboxEvent);
        log.info("[Outbox] 재고 감소 이벤트 저장 완료 (아직 커밋 전)");

        //log.error("[결제] 결제 실패 ! 예외 발생");
        //throw new RuntimeException("결제 처리 중 장애가 발생했습니다.");
    }
}
