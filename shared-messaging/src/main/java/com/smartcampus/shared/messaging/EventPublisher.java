package com.smartcampus.shared.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(CampusEvent event) {
        if (event.getEventId() == null || event.getEventId().isBlank()) {
            event.setEventId(java.util.UUID.randomUUID().toString());
        }
        if (event.getOccurredAt() == null || event.getOccurredAt().isBlank()) {
            event.setOccurredAt(java.time.Instant.now().toString());
        }
        String routingKey = event.getRoutingKey();
        rabbitTemplate.convertAndSend(RabbitMqConstants.EXCHANGE, routingKey, event);
        log.info("Published event {} with routing key {}", event.getEventType(), routingKey);
    }

    public void publishAsync(CampusEvent event) {
        new Thread(() -> publish(event)).start();
    }
}
