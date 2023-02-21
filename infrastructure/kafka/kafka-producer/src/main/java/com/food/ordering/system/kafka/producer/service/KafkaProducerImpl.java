package com.food.ordering.system.kafka.producer.service;

import com.food.ordering.system.kafka.producer.exception.KafkaProduceException;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.Serializable;

@Component
@Slf4j
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, K key, V message, ListenableFutureCallback<SendResult<K, V>> callback) {
        log.info("Sending message={} to topic={}", message, topicName);

        try {
            ListenableFuture<SendResult<K, V>> resultListenableFuture = kafkaTemplate.send(topicName, key, message);
            resultListenableFuture.addCallback(callback);
        } catch (KafkaException e) {
            log.error("Error on kafka producer with key: {}, message: {}, and exception: {}", key, message, e.getMessage());
            throw new KafkaProduceException("Error on kafka producer with key: %s, message: %s, and exception: %s"
                    .formatted(key, message, e.getMessage()));
        }
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Closing kafka producer.");
            kafkaTemplate.destroy();
        }
    }
}
