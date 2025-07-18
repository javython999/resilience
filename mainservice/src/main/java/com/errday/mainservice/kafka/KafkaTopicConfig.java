package com.errday.mainservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String EMAIL_SEND_REQUEST_TOPIC = "email-send-requests";

    @Bean
    public NewTopic emailSendRequestTopic() {
        // email-send-requests 토픽을 파티션 2개, 레플리카 3개로 정의
        return TopicBuilder.name(EMAIL_SEND_REQUEST_TOPIC)
                .partitions(2)
                .replicas(3) // Docker Compose에 설정된 브로커 수와 일치
                .build();
    }

}
