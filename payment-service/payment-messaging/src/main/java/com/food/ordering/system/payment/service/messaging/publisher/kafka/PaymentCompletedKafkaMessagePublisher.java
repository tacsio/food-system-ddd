package com.food.ordering.system.payment.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentCompletedKafkaMessagePublisher implements PaymentCompletedMessagePublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public PaymentCompletedKafkaMessagePublisher(PaymentMessagingDataMapper paymentMessagingDataMapper, KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer, PaymentServiceConfigData paymentServiceConfigData, KafkaMessageHelper kafkaMessageHelper) {
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.paymentServiceConfigData = paymentServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(PaymentCompletedEvent domainEvent) {
        var orderId = domainEvent.payment().getOrderId().value().toString();
        log.info("Received PaymentCompletedEvent for order id: {}", orderId);

        try {


            var paymentResponseAvroModel = paymentMessagingDataMapper.paymentCompletedEventToPaymentResponseAvroModel(domainEvent);
            var paymentResponseTopicName = paymentServiceConfigData.getPaymentRequestTopicName();
            kafkaProducer.send(paymentResponseTopicName, orderId, paymentResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallback(paymentResponseTopicName, paymentResponseAvroModel, orderId, null, null));
        } catch (Exception e) {
            var errorMsg = "Error while sending PaymentResponseAvroModel message to kafka with order id: {} error: {}"
                    .formatted(orderId, e.getMessage());
            log.error(errorMsg);
        }
    }
}
