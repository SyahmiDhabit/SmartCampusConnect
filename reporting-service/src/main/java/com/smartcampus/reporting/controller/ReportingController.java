package com.smartcampus.reporting.controller;

import com.smartcampus.reporting.entity.ProgrammeEnrolmentStat;
import com.smartcampus.reporting.entity.ReportMetric;
import com.smartcampus.reporting.repository.ProgrammeEnrolmentStatRepository;
import com.smartcampus.reporting.repository.ReportMetricRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reports")
public class ReportingController {

    private final ProgrammeEnrolmentStatRepository programmeRepository;
    private final ReportMetricRepository metricRepository;
    private final RestTemplate restTemplate;
    private final String studentServiceUrl;
    private final String enrolmentServiceUrl;
    private final String libraryServiceUrl;

    public ReportingController(ProgrammeEnrolmentStatRepository programmeRepository,
                               ReportMetricRepository metricRepository,
                               @Value("${smartcampus.student-profile.base-url}") String studentBaseUrl,
                               @Value("${smartcampus.course-enrolment.base-url}") String enrolmentBaseUrl,
                               @Value("${smartcampus.library.base-url}") String libraryBaseUrl) {
        this.programmeRepository = programmeRepository;
        this.metricRepository = metricRepository;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1000);
        factory.setReadTimeout(1500);
        this.restTemplate = new RestTemplate(factory);
        this.studentServiceUrl = studentBaseUrl + "/api/students";
        this.enrolmentServiceUrl = enrolmentBaseUrl + "/api/enrolments";
        this.libraryServiceUrl = libraryBaseUrl + "/api/loans";
    }

    @GetMapping("/enrolment-summary")
    public Map<String, Object> getEnrolmentSummaryReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportTitle", "SmartCampus Connect - Enrolment & System Analytics");
        report.put("generatedAt", Instant.now().toString());
        report.put("dataSource", "Event-driven reporting database (RabbitMQ consumers)");

        Map<String, Integer> enrolmentsPerProgramme = new TreeMap<>();
        int confirmed = 0;
        int provisional = 0;
        for (ProgrammeEnrolmentStat stat : programmeRepository.findAll()) {
            enrolmentsPerProgramme.put(stat.getProgramme(), stat.getEnrolmentCount());
            confirmed += stat.getConfirmedCount();
            provisional += stat.getProvisionalCount();
        }

        report.put("enrolmentsPerProgramme", enrolmentsPerProgramme);
        report.put("enrolmentStatuses", Map.of("CONFIRMED", confirmed, "PROVISIONAL", provisional));
        report.put("metrics", readMetrics());
        report.put("liveServiceStatuses", probeLiveServices());
        return report;
    }

    @GetMapping("/enrolments/by-programme")
    public List<ProgrammeEnrolmentStat> getEnrolmentsByProgramme() {
        return programmeRepository.findAll();
    }

    @GetMapping("/metrics")
    public List<ReportMetric> getMetrics() {
        return metricRepository.findAll();
    }

    private Map<String, Long> readMetrics() {
        Map<String, Long> metrics = new LinkedHashMap<>();
        for (ReportMetric metric : metricRepository.findAll()) {
            metrics.put(metric.getMetricKey(), metric.getMetricValue());
        }
        return metrics;
    }

    private Map<String, String> probeLiveServices() {
        Map<String, String> statuses = new LinkedHashMap<>();
        statuses.put("student-profile-service", probe(studentServiceUrl));
        statuses.put("course-enrolment-service", probe(enrolmentServiceUrl));
        statuses.put("library-service", probe(libraryServiceUrl));
        return statuses;
    }

    private String probe(String url) {
        try {
            restTemplate.getForObject(url, List.class);
            return "ONLINE";
        } catch (Exception e) {
            return "OFFLINE";
        }
    }
}
