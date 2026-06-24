package com.smartcampus.library.soap;

import com.smartcampus.library.controller.LibraryController;
import com.smartcampus.library.entity.BookLoan;
import com.smartcampus.library.entity.RoomBooking;
import com.smartcampus.library.repository.BookLoanRepository;
import com.smartcampus.library.repository.RoomBookingRepository;
import com.smartcampus.library.service.LibraryEventPublisher;
import jakarta.jws.WebService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@WebService(
        endpointInterface = "com.smartcampus.library.soap.LibrarySOAPService",
        serviceName = "LibraryService",
        targetNamespace = "http://soap.library.smartcampus.com/"
)
public class LibrarySOAPServiceImpl implements LibrarySOAPService {

    private final BookLoanRepository loanRepository;
    private final RoomBookingRepository bookingRepository;
    private final LibraryEventPublisher libraryEventPublisher;

    public LibrarySOAPServiceImpl(BookLoanRepository loanRepository,
                                  RoomBookingRepository bookingRepository,
                                  LibraryEventPublisher libraryEventPublisher) {
        this.loanRepository = loanRepository;
        this.bookingRepository = bookingRepository;
        this.libraryEventPublisher = libraryEventPublisher;
    }

    @Override
    public String bookLoan(String bookId, String studentId) throws BookAlreadyLoanedFault {
        if (bookId == null || bookId.trim().isEmpty() || studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("bookId and studentId must not be empty");
        }

        Optional<BookLoan> existingLoan = loanRepository.findByBookIdAndStatus(bookId, "ACTIVE");
        if (existingLoan.isPresent()) {
            throw new BookAlreadyLoanedFault("Book already loaned: " + bookId);
        }

        BookLoan loan = new BookLoan(bookId, LibraryController.getBookTitleById(bookId), studentId);
        BookLoan saved = loanRepository.save(loan);
        libraryEventPublisher.publishLoanCreated(saved);
        return "SUCCESS: Book '" + saved.getBookTitle() + "' successfully loaned to student " + studentId;
    }

    @Override
    public String reserveRoom(String roomId, String studentId, String bookingDate, String timeSlot)
            throws RoomAlreadyBookedFault {
        if (roomId == null || studentId == null || bookingDate == null || timeSlot == null) {
            throw new IllegalArgumentException("roomId, studentId, bookingDate, and timeSlot are required");
        }

        boolean clash = bookingRepository.findAll().stream().anyMatch(booking ->
                booking.getRoomId().equalsIgnoreCase(roomId)
                        && booking.getBookingDate().equals(bookingDate)
                        && booking.getTimeSlot().equals(timeSlot)
                        && "CONFIRMED".equals(booking.getStatus()));

        if (clash) {
            throw new RoomAlreadyBookedFault("Room already booked: " + roomId + " on " + bookingDate + " " + timeSlot);
        }

        String roomName = switch (roomId.toUpperCase()) {
            case "R101" -> "Discussion Room A";
            case "R102" -> "Discussion Room B";
            default -> "General Study Room";
        };

        RoomBooking booking = new RoomBooking(roomId, roomName, studentId, bookingDate, timeSlot);
        RoomBooking saved = bookingRepository.save(booking);
        libraryEventPublisher.publishBookingCreated(saved);
        return "SUCCESS: Room " + roomName + " reserved for student " + studentId;
    }
}
