package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {

    private final RestaurantApprovalMessageListener restaurantApprovalMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public RestaurantApprovalResponseKafkaListener(RestaurantApprovalMessageListener restaurantApprovalMessageListener, OrderMessagingDataMapper orderMessagingDataMapper) {
        this.restaurantApprovalMessageListener = restaurantApprovalMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(@Payload List<RestaurantApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("{} number of payment responses received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys, partitions, offsets);


        messages.forEach(it -> {
            switch (it.getOrderApprovalStatus()) {
                case APPROVED -> {
                    log.info("Processing approved order for order id: {}", it.getOrderId());
                    var restaurantApproveResponse = orderMessagingDataMapper.restaurantApproveResponseAvroModelToApprovalResponse(it);
                    restaurantApprovalMessageListener.orderApproved(restaurantApproveResponse);
                }
                case REJECTED -> {
                    log.info("Processing rejected order for order id: {} with failure message: {}", it.getOrderId(), it.getFailureMessages());
                    var restaurantApproveResponse = orderMessagingDataMapper.restaurantApproveResponseAvroModelToApprovalResponse(it);
                    restaurantApprovalMessageListener.orderRejected(restaurantApproveResponse);
                }
            }
        });
    }
}
