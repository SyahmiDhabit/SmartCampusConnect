package com.smartcampus.library.controller;

import com.smartcampus.library.entity.BookLoan;
import com.smartcampus.library.repository.BookLoanRepository;
import com.smartcampus.library.service.LibraryEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/loans")
public class LibraryController {

    private final BookLoanRepository repository;
    private final LibraryEventPublisher libraryEventPublisher;

    public LibraryController(BookLoanRepository repository, LibraryEventPublisher libraryEventPublisher) {
        this.repository = repository;
        this.libraryEventPublisher = libraryEventPublisher;
    }

    @GetMapping
    public List<BookLoan> getAllLoans() {
        return repository.findAll();
    }

    @GetMapping("/student/{studentId}")
    public List<BookLoan> getLoansByStudent(@PathVariable String studentId) {
        return repository.findByStudentId(studentId);
    }

    @PostMapping
    public ResponseEntity<BookLoan> createLoan(@RequestBody BookLoan loanRequest) {
        String bookId = loanRequest.getBookId();
        String studentId = loanRequest.getStudentId();

        if (bookId == null || bookId.trim().isEmpty() || studentId == null || studentId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookId and studentId are required");
        }

        Optional<BookLoan> existing = repository.findByBookIdAndStatus(bookId, "ACTIVE");
        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book " + bookId + " is already loaned out");
        }

        BookLoan loan = new BookLoan(bookId, getBookTitleById(bookId), studentId);
        BookLoan savedLoan = repository.save(loan);
        libraryEventPublisher.publishLoanCreated(savedLoan);
        return new ResponseEntity<>(savedLoan, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/return")
    public BookLoan returnBook(@PathVariable Long id) {
        BookLoan loan = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan record not found with ID: " + id));

        if ("RETURNED".equals(loan.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book was already returned");
        }

        loan.setStatus("RETURNED");
        return repository.save(loan);
    }

    public static String getBookTitleById(String bookId) {
        return switch (bookId.toUpperCase()) {
            case "B101" -> "Introduction to Distributed Systems";
            case "B102" -> "Clean Architecture";
            case "B103" -> "Design Patterns";
            case "B104" -> "Refactoring: Improving the Design of Existing Code";
            case "B105" -> "The Pragmatic Programmer";
            case "B106" -> "Domain-Driven Design";
            case "B107" -> "Code Complete";
            case "B108" -> "Head First Java";
            case "B109" -> "Effective Java";
            case "B110" -> "Cracking the Coding Interview";
            case "B111" -> "Introduction to Algorithms";
            case "B112" -> "Artificial Intelligence: A Modern Approach";
            case "B113" -> "Computer Networking: A Top-Down Approach";
            case "B114" -> "Database System Concepts";
            case "B115" -> "Operating System Concepts";
            case "B116" -> "Structure and Interpretation of Computer Programs";
            case "B117" -> "Modern Operating Systems";
            case "B118" -> "Compilers: Principles, Techniques, and Tools";
            case "B119" -> "The Mythical Man-Month";
            case "B120" -> "Continuous Delivery";
            default -> "Unknown Library Book Reference";
        };
    }
}
