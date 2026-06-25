package com.smartcampus.notification.controller;

import com.smartcampus.notification.entity.NotificationRecord;
import com.smartcampus.notification.registry.NotificationRegistry;
import com.smartcampus.notification.repository.NotificationRecordRepository;
import com.smartcampus.shared.messaging.Message;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRegistry registry;
    private final NotificationRecordRepository repository;

    public NotificationController(NotificationRegistry registry, NotificationRecordRepository repository) {
        this.registry = registry;
        this.repository = repository;
    }

    @GetMapping
    public List<NotificationRecord> getAllNotifications() {
        return repository.findAll();
    }

    @GetMapping("/legacy")
    public List<Message> getLegacyNotifications() {
        return registry.getNotifications();
    }

    @GetMapping("/count")
    public int getNotificationCount() {
        return registry.getEventCounter();
    }

    @DeleteMapping
    @Transactional
    public Map<String, String> clearNotifications() {
        repository.deleteAll();
        registry.clear();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notifications cleared successfully.");
        return response;
    }
}
