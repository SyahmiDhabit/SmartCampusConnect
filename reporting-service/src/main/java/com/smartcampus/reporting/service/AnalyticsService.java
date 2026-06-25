package com.smartcampus.reporting.service;

import com.smartcampus.reporting.entity.ProcessedEvent;
import com.smartcampus.reporting.entity.ProgrammeEnrolmentStat;
import com.smartcampus.reporting.entity.ReportMetric;
import com.smartcampus.reporting.repository.ProcessedEventRepository;
import com.smartcampus.reporting.repository.ProgrammeEnrolmentStatRepository;
import com.smartcampus.reporting.repository.ReportMetricRepository;
import com.smartcampus.shared.messaging.CampusEvent;
import com.smartcampus.shared.messaging.RabbitMqConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AnalyticsService {

    private final ProgrammeEnrolmentStatRepository programmeRepository;
    private final ReportMetricRepository metricRepository;
    private final ProcessedEventRepository processedEventRepository;

    public AnalyticsService(ProgrammeEnrolmentStatRepository programmeRepository,
                            ReportMetricRepository metricRepository,
                            ProcessedEventRepository processedEventRepository) {
        this.programmeRepository = programmeRepository;
        this.metricRepository = metricRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void processEvent(CampusEvent event) {
        if (event.getEventId() != null && processedEventRepository.existsById(event.getEventId())) {
            return;
        }

        switch (event.getEventType()) {
            case RabbitMqConstants.ET_ENROLMENT_CREATED -> handleEnrolmentCreated(event.getData());
            case RabbitMqConstants.ET_ENROLMENT_DROPPED -> handleEnrolmentDropped(event.getData());
            case RabbitMqConstants.ET_LIBRARY_LOAN_CREATED -> incrementMetric("total_library_loans");
            case RabbitMqConstants.ET_LIBRARY_BOOKING_CREATED -> incrementMetric("total_room_bookings");
            default -> {
            }
        }

        if (event.getEventId() != null) {
            processedEventRepository.save(new ProcessedEvent(event.getEventId()));
        }
    }

    private void handleEnrolmentCreated(Map<String, Object> data) {
        if (data == null) {
            return;
        }
        String programme = String.valueOf(data.getOrDefault("programme", "Unknown"));
        String status = String.valueOf(data.getOrDefault("status", "CONFIRMED"));

        ProgrammeEnrolmentStat stat = programmeRepository.findById(programme)
                .orElseGet(() -> new ProgrammeEnrolmentStat(programme));
        stat.setEnrolmentCount(stat.getEnrolmentCount() + 1);
        if ("PROVISIONAL".equalsIgnoreCase(status)) {
            stat.setProvisionalCount(stat.getProvisionalCount() + 1);
        } else {
            stat.setConfirmedCount(stat.getConfirmedCount() + 1);
        }
        programmeRepository.save(stat);
        incrementMetric("total_enrolments");
    }

    private void handleEnrolmentDropped(Map<String, Object> data) {
        incrementMetric("total_enrolment_drops");
        if (data != null && data.containsKey("programme")) {
            String programme = String.valueOf(data.get("programme"));
            programmeRepository.findById(programme).ifPresent(stat -> {
                stat.setEnrolmentCount(Math.max(0, stat.getEnrolmentCount() - 1));
                programmeRepository.save(stat);
            });
        }
    }

    private void incrementMetric(String key) {
        ReportMetric metric = metricRepository.findById(key).orElse(new ReportMetric(key, 0));
        metric.setMetricValue(metric.getMetricValue() + 1);
        metricRepository.save(metric);
    }
}
