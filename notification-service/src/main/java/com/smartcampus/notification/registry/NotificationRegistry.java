package com.smartcampus.notification.registry;

import com.smartcampus.shared.messaging.Message;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class NotificationRegistry {
    // Shared mutable state
    private final List<Message> notifications = new ArrayList<>();
    private int eventCounter = 0;
    
    // Lock to protect shared mutable state (R5 requirement)
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Thread-safely appends a message and increments the event counter.
     */
    public void addNotification(Message message) {
        lock.lock();
        try {
            notifications.add(message);
            eventCounter++;
            System.out.println("[NotificationRegistry] Event logged. Total Count: " + eventCounter 
                    + " | Type: " + message.getEventType() + " from: " + message.getSender());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Thread-safely retrieves a copy of all received messages.
     */
    public List<Message> getNotifications() {
        lock.lock();
        try {
            // Return a shallow copy of the list to prevent external modification of the internal list
            return new ArrayList<>(notifications);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Thread-safely retrieves the current event count.
     */
    public int getEventCounter() {
        lock.lock();
        try {
            return eventCounter;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Clears all notifications (useful for testing).
     */
    public void clear() {
        lock.lock();
        try {
            notifications.clear();
            eventCounter = 0;
        } finally {
            lock.unlock();
        }
    }
}
