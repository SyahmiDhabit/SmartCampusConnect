package com.smartcampus.shared.messaging;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Standard RabbitMQ event envelope for SmartCampus Connect.
 */
public class CampusEvent {

    private String eventId;
    private String eventType;
    private String routingKey;
    private String source;
    private String occurredAt;
    private Map<String, Object> data;

    public CampusEvent() {
    }

    public CampusEvent(String eventType, String routingKey, String source, Map<String, Object> data) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.routingKey = routingKey;
        this.source = source;
        this.occurredAt = Instant.now().toString();
        this.data = data != null ? data : new LinkedHashMap<>();
    }

    public static CampusEvent of(String eventType, String routingKey, String source, Map<String, Object> data) {
        return new CampusEvent(eventType, routingKey, source, data);
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

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Message toLegacyMessage() {
        String payload = data != null && data.containsKey("summary")
                ? String.valueOf(data.get("summary"))
                : eventType;
        return new Message(source, eventType, payload);
    }
}
