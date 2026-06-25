package com.smartcampus.notification.repository;

import com.smartcampus.notification.entity.NotificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRecordRepository extends JpaRepository<NotificationRecord, Long> {
    boolean existsByEventId(String eventId);
}
