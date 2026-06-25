package com.smartcampus.reporting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reporting_events")
public class ReportingEvent {

    @Id
    private String eventId;

    private String eventType;
    private String routingKey;
    private String source;
    private String occurredAt;

    private String payload;

    public ReportingEvent() {
    }

    public ReportingEvent(String eventId, String eventType, String routingKey,
                          String source, String occurredAt, String payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.routingKey = routingKey;
        this.source = source;
        this.occurredAt = occurredAt;
        this.payload = payload;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(String occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}