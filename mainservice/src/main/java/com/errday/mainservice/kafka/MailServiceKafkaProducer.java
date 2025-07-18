package com.errday.mainservice.kafka;

import com.errday.mainservice.mail.EmailRequest;
import com.errday.mainservice.mail.MailServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceKafkaProducer implements MailServiceClient {

    // KafkaTemplate의 Value 타입을 EmailRequest로 변경
    private final KafkaTemplate<String, EmailRequest> kafkaTemplate;

    @Override
    public String sendMail(EmailRequest emailRequest) {
        if (emailRequest == null || emailRequest.getEmail() == null || emailRequest.getEmail().isEmpty()) {
            log.error("EmailRequest 또는 이메일 주소가 null이거나 비어있습니다.");
            return "Failed to send email request: Email address is missing.";
        }

        String key = emailRequest.getEmail();   // EmailRequest의 email 필드를 Kafka 메시지 키로 사용
        EmailRequest value = emailRequest;      // EmailRequest 객체 자체를 Kafka 메시지 값으로 사용

        try {
            // 키(파티셔닝 기준)와 값(EmailRequest 객체)을 함께 전송
            CompletableFuture<SendResult<String, EmailRequest>> future = kafkaTemplate.send(KafkaTopicConfig.EMAIL_SEND_REQUEST_TOPIC, key, value);

            future.whenComplete((result, exception) -> {
                if (exception != null) {
                    log.info(
                            "메일 발송 요청 완료. Offset: {}, Partition: {}, Key: {}",
                            result.getRecordMetadata().offset(),
                            result.getRecordMetadata().partition(),
                            key
                    );
                } else {
                    log.error("Asynchronous confirmmation: Failed to send email for key {}. Cause: {}", key, exception.getMessage() != null ? exception.getCause() : exception);
                }
            });

            log.info("Successfully SUBMITTED request to kafka for key: {}", key);
            return "Email request for " + key + " sent to Kafka.";

        } catch (Exception e) {
            log.error("Failed to send EmailRequest to Kafka topic {} with key {}: {}", KafkaTopicConfig.EMAIL_SEND_REQUEST_TOPIC, key, value, e);
            throw new RuntimeException("Failed to submit to message to Kafka", e);
        }
    }
}
