package com.smartcampus.library.service;

import com.smartcampus.library.entity.BookLoan;
import com.smartcampus.library.entity.RoomBooking;
import com.smartcampus.shared.messaging.CampusEvent;
import com.smartcampus.shared.messaging.EventPublisher;
import com.smartcampus.shared.messaging.RabbitMqConstants;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LibraryEventPublisher {

    private final EventPublisher eventPublisher;

    public LibraryEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishLoanCreated(BookLoan loan) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("loanId", loan.getId());
        data.put("bookId", loan.getBookId());
        data.put("bookTitle", loan.getBookTitle());
        data.put("studentId", loan.getStudentId());
        data.put("summary", String.format("Book Loaned: '%s' (%s) to Student %s",
                loan.getBookTitle(), loan.getBookId(), loan.getStudentId()));

        CampusEvent event = CampusEvent.of(
                RabbitMqConstants.ET_LIBRARY_LOAN_CREATED,
                RabbitMqConstants.RK_LIBRARY_LOAN_CREATED,
                "library-booking-service",
                data);
        eventPublisher.publishAsync(event);
    }

    public void publishBookingCreated(RoomBooking booking) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("bookingId", booking.getId());
        data.put("roomId", booking.getRoomId());
        data.put("roomName", booking.getRoomName());
        data.put("studentId", booking.getStudentId());
        data.put("bookingDate", booking.getBookingDate());
        data.put("timeSlot", booking.getTimeSlot());
        data.put("summary", String.format("Room %s booked by Student %s on %s (%s)",
                booking.getRoomName(), booking.getStudentId(), booking.getBookingDate(), booking.getTimeSlot()));

        CampusEvent event = CampusEvent.of(
                RabbitMqConstants.ET_LIBRARY_BOOKING_CREATED,
                RabbitMqConstants.RK_LIBRARY_BOOKING_CREATED,
                "library-booking-service",
                data);
        eventPublisher.publishAsync(event);
    }
}
