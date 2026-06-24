package com.smartcampus.library.repository;

import com.smartcampus.library.entity.BookLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    List<BookLoan> findByStudentId(String studentId);
    Optional<BookLoan> findByBookIdAndStatus(String bookId, String status);
}
