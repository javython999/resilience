package com.errday.orderservice.kafka;

import com.errday.orderservice.outbox.OutboxEvent;
import com.errday.orderservice.outbox.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaProducerService kafkaProducerService;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void relayOutboxMessages() {
        List<OutboxEvent> pendingMessages = outboxEventRepository.findByStatus(OutboxEvent.EventStatus.PENDING);

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.info("[Relay] 아웃박스에서 {}개의 이벤트를 발견! 릴레이를 시작합니다.", pendingMessages.size());

        for (OutboxEvent event : pendingMessages) {
            event.changeStatus(OutboxEvent.EventStatus.PROCESSING);

            outboxEventRepository.save(event);

            try {
                if ("STOCK_DECREASED".equals(event.getEventType())) {
                    kafkaProducerService.send("stock-decrease-topic", event.getPayload());
                    log.info("☑️ [Relay] '재고 감소' 이벤트 발행 성공 (ID: {})", event.getId());
                } else if ("STOCK_INCREASED".equals(event.getEventType())) {
                    kafkaProducerService.send("stock-increase-topic", event.getPayload());
                    log.info("☑️ [Relay] '재고 증가' 보상 이벤트 발행 성공 (ID: {})", event.getId());
                }

                event.changeStatus(OutboxEvent.EventStatus.PUBLISHED);
                outboxEventRepository.save(event);
            } catch (Exception e) {
                log.error("[Relay] 이벤트 발행 실패 ID: {}", event.getId(), e);
                event.changeStatus(OutboxEvent.EventStatus.FAILED);
                outboxEventRepository.save(event);
            }
        }
    }
}
