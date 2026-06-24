package com.smartcampus.enrolment.repository;

import com.smartcampus.enrolment.entity.CachedStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachedStudentRepository extends JpaRepository<CachedStudent, String> {
}
