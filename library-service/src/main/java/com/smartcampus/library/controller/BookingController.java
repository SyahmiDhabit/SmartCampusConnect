package com.smartcampus.library.controller;

import com.smartcampus.library.entity.RoomBooking;
import com.smartcampus.library.repository.RoomBookingRepository;
import com.smartcampus.library.service.LibraryEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final RoomBookingRepository repository;
    private final LibraryEventPublisher libraryEventPublisher;

    public BookingController(RoomBookingRepository repository, LibraryEventPublisher libraryEventPublisher) {
        this.repository = repository;
        this.libraryEventPublisher = libraryEventPublisher;
    }

    @GetMapping
    public List<RoomBooking> getAllBookings() {
        return repository.findAll();
    }

    @GetMapping("/student/{studentId}")
    public List<RoomBooking> getBookingsByStudent(@PathVariable String studentId) {
        return repository.findByStudentId(studentId);
    }

    @PostMapping
    public ResponseEntity<RoomBooking> createBooking(@RequestBody RoomBooking request) {
        if (repository.existsByRoomIdAndBookingDateAndTimeSlot(request.getRoomId(), request.getBookingDate(), request.getTimeSlot())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This room is already reserved for the selected date and time slot.");
        }

        String roomName = resolveRoomName(request.getRoomId());
        RoomBooking booking = new RoomBooking(
                request.getRoomId(),
                roomName,
                request.getStudentId(),
                request.getBookingDate(),
                request.getTimeSlot()
        );
        RoomBooking saved = repository.save(booking);
        libraryEventPublisher.publishBookingCreated(saved);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    private String resolveRoomName(String roomId) {
        return switch (roomId.toUpperCase()) {
            case "R101" -> "Discussion Room A";
            case "R102" -> "Discussion Room B";
            case "R103" -> "Project Room C";
            default -> "General Study Room";
        };
    }
}
