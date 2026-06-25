package com.smartcampus.notification.messaging;

import com.smartcampus.notification.entity.NotificationRecord;
import com.smartcampus.notification.registry.NotificationRegistry;
import com.smartcampus.notification.repository.NotificationRecordRepository;
import com.smartcampus.shared.messaging.CampusEvent;
import com.smartcampus.shared.messaging.RabbitMqConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CampusEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(CampusEventConsumer.class);

    private final NotificationRecordRepository repository;
    private final NotificationRegistry registry;

    public CampusEventConsumer(NotificationRecordRepository repository, NotificationRegistry registry) {
        this.repository = repository;
        this.registry = registry;
    }

    @RabbitListener(queues = RabbitMqConstants.NOTIFICATION_QUEUE, concurrency = "5-10")
    @Transactional
    public void handleEvent(CampusEvent event) {
        if (event.getEventId() != null && repository.existsByEventId(event.getEventId())) {
            log.info("Skipping duplicate event {}", event.getEventId());
            return;
        }

        String payload = event.getData() != null && event.getData().containsKey("summary")
                ? String.valueOf(event.getData().get("summary"))
                : event.getEventType();

        NotificationRecord record = new NotificationRecord(
                event.getEventId(),
                event.getEventType(),
                event.getSource(),
                event.getRoutingKey(),
                payload
        );
        repository.save(record);
        registry.addNotification(event.toLegacyMessage());

        log.info("Processed notification event {} from {}", event.getEventType(), event.getSource());
    }
}
