package com.smartcampus.service;

import com.smartcampus.reporting.entity.ReportingEvent;
import com.smartcampus.reporting.repository.ReportingEventRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final ReportingEventRepository repository;

    public AnalyticsService(ReportingEventRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getEnrolmentSummary() {
        List<ReportingEvent> events = repository.findAll();

        long created = events.stream()
                .filter(e -> "ENROLMENT_CREATED".equals(e.getEventType()))
                .count();

        long dropped = events.stream()
                .filter(e -> "ENROLMENT_DROPPED".equals(e.getEventType()))
                .count();

        long active = created - dropped;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCreated", created);
        summary.put("totalDropped", dropped);
        summary.put("activeEnrolments", active);
        summary.put("totalEvents", events.size());

        return summary;
    }
}