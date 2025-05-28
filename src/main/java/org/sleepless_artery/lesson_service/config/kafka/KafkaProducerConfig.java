package org.sleepless_artery.lesson_service.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.sleepless_artery.lesson_service.config.kafka.properties.KafkaProducerConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProducerConfigProperties producerConfigProperties;


    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerConfigProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerConfigProperties.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerConfigProperties.getValueSerializer());
        props.put(ProducerConfig.ACKS_CONFIG, producerConfigProperties.getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, producerConfigProperties.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, producerConfigProperties.getBatchSize());
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, producerConfigProperties.getDeliveryTimeoutMs());
        props.put(ProducerConfig.LINGER_MS_CONFIG, producerConfigProperties.getLingerMs());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, producerConfigProperties.getRequestTimeoutMs());
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, producerConfigProperties.getRetryBackoffMs());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
