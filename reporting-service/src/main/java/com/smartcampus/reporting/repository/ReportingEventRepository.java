package com.smartcampus.reporting.repository;

import com.smartcampus.reporting.entity.ReportingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportingEventRepository extends JpaRepository<ReportingEvent, String> {
}