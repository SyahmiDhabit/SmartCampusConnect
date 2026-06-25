package com.smartcampus.reporting.messaging;

import com.smartcampus.reporting.entity.ReportingEvent;
import com.smartcampus.reporting.repository.ReportingEventRepository;
import com.smartcampus.shared.messaging.CampusEvent;
import com.smartcampus.shared.messaging.RabbitMqConstants;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReportingEventConsumer {

    private final ReportingEventRepository repository;

    public ReportingEventConsumer(ReportingEventRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitMqConstants.REPORTING_QUEUE)
    @Transactional
    public void handleEvent(CampusEvent event) {
        if (event == null || event.getEventId() == null) {
            return;
        }

        if (repository.existsById(event.getEventId())) {
            return;
        }

        String payload = event.getData() != null ? event.getData().toString() : null;

        ReportingEvent reportingEvent = new ReportingEvent(
                event.getEventId(),
                event.getEventType(),
                event.getRoutingKey(),
                event.getSource(),
                event.getOccurredAt(),
                payload
        );

        repository.save(reportingEvent);
    }
}