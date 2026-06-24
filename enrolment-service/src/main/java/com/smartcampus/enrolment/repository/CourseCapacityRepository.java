package com.smartcampus.enrolment.repository;

import com.smartcampus.enrolment.entity.CourseCapacity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseCapacityRepository extends JpaRepository<CourseCapacity, String> {
}
