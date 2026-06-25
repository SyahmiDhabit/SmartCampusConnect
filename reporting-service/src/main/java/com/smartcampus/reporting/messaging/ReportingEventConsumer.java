package com.smartcampus.reporting.messaging;

import com.smartcampus.reporting.service.AnalyticsService;
import com.smartcampus.shared.messaging.CampusEvent;
import com.smartcampus.shared.messaging.RabbitMqConstants;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReportingEventConsumer {

    private final AnalyticsService analyticsService;

    public ReportingEventConsumer(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @RabbitListener(queues = RabbitMqConstants.REPORTING_QUEUE, concurrency = "2-5")
    public void handleEvent(CampusEvent event) {
        analyticsService.processEvent(event);
    }
}
