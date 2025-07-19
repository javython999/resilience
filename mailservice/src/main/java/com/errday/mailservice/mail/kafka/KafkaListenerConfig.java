package com.errday.mailservice.mail.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;

@Slf4j
@Configuration
public class KafkaListenerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            CommonErrorHandler loggingErrorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(loggingErrorHandler);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        log.info("Configured ConcurrentKafkaListenerContainerFactory with custom DefaultErrorHandler.");
        return factory;
    }

    @Bean
    public CommonErrorHandler loggingErrorHandler() {
        log.info("Configured CommonErrorHandler.");
        return new CommonLoggingErrorHandler();
    }
}