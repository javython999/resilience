package com.errday.orderservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockDecreaseConsumer {

    @KafkaListener(topics = "stock-decrease-topic", groupId = "order-group")
    public void listen(String message) {
        log.warn("🚚 [Consumer] 재고 감소 이벤트 수신 성공! 메시지: {}", message);
    }
}
