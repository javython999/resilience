package com.errday.orderservice.order;

import com.errday.orderservice.kafka.KafkaProducerService;
import com.errday.orderservice.outbox.OutboxEvent;
import com.errday.orderservice.outbox.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    public void placeOrderSaga(OrderRequestDto request) {
        Order order = null;
        boolean paymentSuccess = false;

        try {
            order = createOrderAndStockDecreaseEvent(request);

            processPayment(order.getId());
            paymentSuccess = true;

            updateOrderStatus(order);
        } catch (Exception e) {
            log.error("SAGA 실패! 보상 트랜잭션을 시작합니다. 원인: {}", e.getMessage());

            if (paymentSuccess) {
                compensatePayment(order.getId());
            }

            if (order != null) {
                triggerStockIncrease(request);
                cancelOrder(order);
            }
        }
    }

    @Transactional
    public Order createOrderAndStockDecreaseEvent(OrderRequestDto request) {
        log.info("[SAGA 1,2] 주문 생성 및 재고 감소 이벤트 저장 트랜잭션 시작");

        Order order = orderRepository.save(new Order(request.productName(), request.quantity()));
        String payload = request.toJson(objectMapper);

        OutboxEvent outboxEvent = new OutboxEvent("STOCK_DECREASED", payload);
        outboxEventRepository.save(outboxEvent);

        log.info("[SAGA 1,2] 트랜잭션 커밋 완료");
        return order;
    }

    private void processPayment(Long orderId) {
        log.info("[SAGA 3] 외부 결제 API 호출 성공 (주문 ID: {})", orderId);
    }

    @Transactional
    public void updateOrderStatus(Order order) {
        log.info("[SAGA 4] 주문 상태 변경 시도...");
        order.changeStatus("COMPLETED");
        orderRepository.save(order);

        throw new RuntimeException("DB 커넥션 오류로 주문 상태 변경 실패!");
    }

    private void compensatePayment(Long orderId) {
        log.warn("[보상-3] 외부 결제 취소 API 호출 (주문 ID: {})", orderId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void triggerStockIncrease(OrderRequestDto request) {
        log.warn("[보상-2] 재고 증가 보상 이벤트 저장 트랜잭션 시작");

        String payload = request.toJson(objectMapper);
        OutboxEvent compensationEvent = new OutboxEvent("STOCK_INCREASED", payload);
        outboxEventRepository.save(compensationEvent);

        log.warn("[보상-2] 트랜잭션 커밋 완료");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cancelOrder(Order order) {
        log.warn("[보상-1] 주문 상태 'FAILED'로 변경 트랜잭션 시작 (주문 ID: {})", order.getId());

        order.changeStatus("FAILED");
        orderRepository.save(order);

        log.warn("[보상-1] 트랜잭션 커밋 완료");
    }
}
