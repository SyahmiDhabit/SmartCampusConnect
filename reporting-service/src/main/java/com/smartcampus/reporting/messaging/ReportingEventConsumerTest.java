package com.smartcampus.reporting.messaging;

import com.smartcampus.reporting.entity.ReportingEvent;
import com.smartcampus.reporting.repository.ReportingEventRepository;
import com.smartcampus.shared.messaging.CampusEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReportingEventConsumerTest {

    @Test
    void handleEvent_savesNewEvent() {
        ReportingEventRepository repository = mock(ReportingEventRepository.class);
        when(repository.existsById("evt-1")).thenReturn(false);

        ReportingEventConsumer consumer = new ReportingEventConsumer(repository);

        CampusEvent event = CampusEvent.of(
                "ENROLMENT_CREATED",
                "enrolment.created",
                "course-enrolment-service",
                Map.of("summary", "Student S1 enrolled in CS101")
        );
        event.setEventId("evt-1");
        event.setOccurredAt("2026-06-25T00:00:00Z");

        consumer.handleEvent(event);

        ArgumentCaptor<ReportingEvent> captor = ArgumentCaptor.forClass(ReportingEvent.class);
        verify(repository).save(captor.capture());

        ReportingEvent saved = captor.getValue();
        assertEquals("evt-1", saved.getEventId());
        assertEquals("ENROLMENT_CREATED", saved.getEventType());
        assertEquals("enrolment.created", saved.getRoutingKey());
    }

    @Test
    void handleEvent_ignoresDuplicateEvent() {
        ReportingEventRepository repository = mock(ReportingEventRepository.class);
        when(repository.existsById("evt-1")).thenReturn(true);

        ReportingEventConsumer consumer = new ReportingEventConsumer(repository);

        CampusEvent event = CampusEvent.of(
                "ENROLMENT_CREATED",
                "enrolment.created",
                "course-enrolment-service",
                Map.of("summary", "duplicate")
        );
        event.setEventId("evt-1");

        consumer.handleEvent(event);

        verify(repository, never()).save(any());
    }
}