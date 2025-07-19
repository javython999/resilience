package com.errday.mailservice.mail.kafka;

import com.errday.mailservice.mail.EmailRequest;
import com.errday.mailservice.mail.MailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailRequestListener {

    private static final String ORIGINAL_TOPIC = "email-send-requests";

    private final MailSenderService mailSenderService;

    @KafkaListener(
            topics = ORIGINAL_TOPIC, // 원본 토픽 구독
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory" // 새로 정의해준 팩토리 사용
    )
    public void consumeEmailRequest(ConsumerRecord<String, EmailRequest> record, Acknowledgment ack) {
        log.info("Kafka 통해 EmailRequest 수신: Topic={}, Partition={}, Offset={}, Key={}, Value={}", record.topic(), record.partition(), record.offset(), record.key(), record.value());

        long currentOffset = record.offset();
        log.info("현재 Offset: {}", currentOffset);

        try {
            log.info("3초 대기 시작...");
            Thread.sleep(3000);
            log.info("3초 대기 완료.");

            ack.acknowledge();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("대기 중 인터럽트 발생: {}", e.getMessage());
        }
    }

}