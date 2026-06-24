package com.smartcampus.enrolment.repository;

import com.smartcampus.enrolment.entity.Enrolment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrolmentRepository extends JpaRepository<Enrolment, Long> {
    List<Enrolment> findByStudentId(String studentId);
    boolean existsByStudentIdAndCourseCode(String studentId, String courseCode);
}
