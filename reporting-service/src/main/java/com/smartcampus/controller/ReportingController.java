package com.smartcampus.controller;

import com.smartcampus.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportingController {

    private final AnalyticsService analyticsService;

    public ReportingController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/enrolment-summary")
    public Map<String, Object> enrolmentSummary() {
        return analyticsService.getEnrolmentSummary();
    }
}