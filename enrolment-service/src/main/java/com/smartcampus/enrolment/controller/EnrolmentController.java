package com.smartcampus.enrolment.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.enrolment.client.StudentClient;
import com.smartcampus.enrolment.entity.Enrolment;
import com.smartcampus.enrolment.repository.EnrolmentRepository;
import com.smartcampus.enrolment.service.EnrolmentService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/enrolments")
public class EnrolmentController {

    private final EnrolmentRepository enrolmentRepository;
    private final EnrolmentService enrolmentService;
    private final StudentClient studentClient;

    public EnrolmentController(EnrolmentRepository enrolmentRepository,
                               EnrolmentService enrolmentService,
                               StudentClient studentClient) {
        this.enrolmentRepository = enrolmentRepository;
        this.enrolmentService = enrolmentService;
        this.studentClient = studentClient;
    }

    @GetMapping
    public List<Enrolment> getAllEnrolments() {
        return enrolmentRepository.findAll();
    }

    @GetMapping("/student/{studentId}")
    public List<Enrolment> getEnrolmentsByStudent(@PathVariable String studentId) {
        return enrolmentRepository.findByStudentId(studentId);
    }

    @GetMapping("/circuit-breaker")
    public Map<String, String> getCircuitStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("targetService", "student-profile-service");
        status.put("state", studentClient.getCircuitBreakerState());
        return status;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> enrolCourse(@RequestBody Enrolment enrolmentRequest) {
        Map<String, Object> response = enrolmentService.enrol(enrolmentRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dropCourse(@PathVariable Long id) {
        enrolmentService.drop(id);
    }
}
