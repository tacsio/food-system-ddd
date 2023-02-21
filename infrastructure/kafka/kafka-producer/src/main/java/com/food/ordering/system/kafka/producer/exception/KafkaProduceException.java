package com.food.ordering.system.kafka.producer.exception;

public class KafkaProduceException extends RuntimeException{

    public KafkaProduceException(String message) {
        super(message);
    }
}
