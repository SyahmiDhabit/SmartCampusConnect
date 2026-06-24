package com.smartcampus.library.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_loans")
public class BookLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookId;
    private String bookTitle;
    private String studentId;
    private long loanDate;
    private String status; // ACTIVE, RETURNED

    public BookLoan() {
        this.loanDate = System.currentTimeMillis();
        this.status = "ACTIVE";
    }

    public BookLoan(String bookId, String bookTitle, String studentId) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.studentId = studentId;
        this.loanDate = System.currentTimeMillis();
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public long getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(long loanDate) {
        this.loanDate = loanDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
