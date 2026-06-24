package com.smartcampus.library.repository;

import com.smartcampus.library.entity.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {
    List<RoomBooking> findByStudentId(String studentId);
    boolean existsByRoomIdAndBookingDateAndTimeSlot(String roomId, String bookingDate, String timeSlot);
}
