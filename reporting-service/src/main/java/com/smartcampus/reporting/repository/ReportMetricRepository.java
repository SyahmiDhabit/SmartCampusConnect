package com.smartcampus.reporting.repository;

import com.smartcampus.reporting.entity.ReportMetric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportMetricRepository extends JpaRepository<ReportMetric, String> {
}
