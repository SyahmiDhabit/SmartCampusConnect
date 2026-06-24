package com.smartcampus.student.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.smartcampus.shared.messaging.CampusEvent;
import com.smartcampus.shared.messaging.EventPublisher;
import com.smartcampus.shared.messaging.RabbitMqConstants;
import com.smartcampus.student.entity.Student;
import com.smartcampus.student.repository.StudentRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository repository;
    private final EventPublisher eventPublisher;

    public StudentController(StudentRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + id));
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        if (student.getId() == null || student.getId().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student ID is required");
        }
        if (repository.existsById(student.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student already exists with ID: " + student.getId());
        }

        Student savedStudent = repository.save(student);
        publishStudentEvent(savedStudent, RabbitMqConstants.ET_STUDENT_CREATED, RabbitMqConstants.RK_STUDENT_CREATED);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable String id, @RequestBody Student studentUpdate) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + id));

        student.setName(studentUpdate.getName());
        student.setEmail(studentUpdate.getEmail());
        student.setProgramme(studentUpdate.getProgramme());
        student.setGpa(studentUpdate.getGpa());

        Student updatedStudent = repository.save(student);
        publishStudentEvent(updatedStudent, RabbitMqConstants.ET_STUDENT_UPDATED, RabbitMqConstants.RK_STUDENT_UPDATED);
        return updatedStudent;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    private void publishStudentEvent(Student student, String eventType, String routingKey) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("studentId", student.getId());
        data.put("name", student.getName());
        data.put("programme", student.getProgramme());
        data.put("summary", "Profile " + eventType.toLowerCase().replace('_', ' ') + " for " + student.getName());

        CampusEvent event = CampusEvent.of(eventType, routingKey, "student-profile-service", data);
        eventPublisher.publishAsync(event);
    }
}
