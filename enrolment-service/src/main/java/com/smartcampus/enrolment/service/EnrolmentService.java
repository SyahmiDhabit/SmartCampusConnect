package com.smartcampus.enrolment.service;

import com.smartcampus.enrolment.client.StudentClient;
import com.smartcampus.enrolment.client.StudentClient.VerificationResult;
import com.smartcampus.enrolment.entity.CachedStudent;
import com.smartcampus.enrolment.entity.CourseCapacity;
import com.smartcampus.enrolment.entity.Enrolment;
import com.smartcampus.enrolment.repository.CachedStudentRepository;
import com.smartcampus.enrolment.repository.CourseCapacityRepository;
import com.smartcampus.enrolment.repository.EnrolmentRepository;
import com.smartcampus.shared.messaging.CampusEvent;
import com.smartcampus.shared.messaging.EventPublisher;
import com.smartcampus.shared.messaging.RabbitMqConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class EnrolmentService {

    private final EnrolmentRepository enrolmentRepository;
    private final CachedStudentRepository cachedStudentRepository;
    private final CourseCapacityRepository capacityRepository;
    private final StudentClient studentClient;
    private final EventPublisher eventPublisher;

    public EnrolmentService(EnrolmentRepository enrolmentRepository,
                            CachedStudentRepository cachedStudentRepository,
                            CourseCapacityRepository capacityRepository,
                            StudentClient studentClient,
                            EventPublisher eventPublisher) {
        this.enrolmentRepository = enrolmentRepository;
        this.cachedStudentRepository = cachedStudentRepository;
        this.capacityRepository = capacityRepository;
        this.studentClient = studentClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public synchronized Map<String, Object> enrol(Enrolment enrolmentRequest) {
        String studentId = enrolmentRequest.getStudentId();
        String courseCode = enrolmentRequest.getCourseCode();
        String semester = enrolmentRequest.getSemester();

        if (studentId == null || courseCode == null || semester == null) {
            throw new ResponseStatusException(BAD_REQUEST, "studentId, courseCode, and semester are required");
        }

        CourseCapacity capacity = capacityRepository.findById(courseCode)
                .orElseGet(() -> capacityRepository.save(new CourseCapacity(courseCode, 30, 0)));

        if (!capacity.hasCapacity()) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Course " + courseCode + " has reached maximum capacity (" + capacity.getMaxCapacity() + ")");
        }

        if (enrolmentRepository.existsByStudentIdAndCourseCode(studentId, courseCode)) {
            throw new ResponseStatusException(BAD_REQUEST, "Student is already enrolled in course: " + courseCode);
        }

        VerificationResult verification = studentClient.verifyStudent(studentId);
        String finalStatus;
        String warningMessage = null;
        String programme = null;

        if (verification.isOffline()) {
            Optional<CachedStudent> cached = cachedStudentRepository.findById(studentId);
            if (cached.isPresent()) {
                finalStatus = "CONFIRMED";
                programme = cached.get().getProgramme();
                warningMessage = "Student Profile Service offline. Verified via local cache.";
            } else {
                finalStatus = "PROVISIONAL";
                warningMessage = "Student Profile Service offline and student not in local cache. Enrolled provisionally.";
            }
        } else {
            if (!verification.exists()) {
                throw new ResponseStatusException(BAD_REQUEST, "Student profile does not exist for ID: " + studentId);
            }
            programme = verification.getProgramme();
            cachedStudentRepository.save(new CachedStudent(studentId, verification.getName(), programme));
            finalStatus = "CONFIRMED";
        }

        Enrolment newEnrolment = enrolmentRepository.save(new Enrolment(studentId, courseCode, semester, finalStatus));
        capacity.setCurrentEnrolled(capacity.getCurrentEnrolled() + 1);
        capacityRepository.save(capacity);

        publishEnrolmentCreated(newEnrolment, programme, finalStatus);
        return buildEnrolmentResponse(newEnrolment, warningMessage);
    }

    @Transactional
    public synchronized void drop(Long id) {
        Enrolment enrolment = enrolmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Enrolment record not found with ID: " + id));

        capacityRepository.findById(enrolment.getCourseCode()).ifPresent(capacity -> {
            if (capacity.getCurrentEnrolled() > 0) {
                capacity.setCurrentEnrolled(capacity.getCurrentEnrolled() - 1);
                capacityRepository.save(capacity);
            }
        });

        enrolmentRepository.delete(enrolment);
        publishEnrolmentDropped(enrolment);
    }

    private void publishEnrolmentCreated(Enrolment enrolment, String programme, String status) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("enrolmentId", enrolment.getId());
        data.put("studentId", enrolment.getStudentId());
        data.put("courseCode", enrolment.getCourseCode());
        data.put("semester", enrolment.getSemester());
        data.put("programme", programme != null ? programme : "Unknown");
        data.put("status", status);
        data.put("summary", String.format("Student %s enrolled in %s (%s). Status: %s",
                enrolment.getStudentId(), enrolment.getCourseCode(),
                enrolment.getSemester(), status));

        CampusEvent event = CampusEvent.of(
                RabbitMqConstants.ET_ENROLMENT_CREATED,
                RabbitMqConstants.RK_ENROLMENT_CREATED,
                "course-enrolment-service",
                data);
        eventPublisher.publishAsync(event);
    }

    private void publishEnrolmentDropped(Enrolment enrolment) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("enrolmentId", enrolment.getId());
        data.put("studentId", enrolment.getStudentId());
        data.put("courseCode", enrolment.getCourseCode());
        data.put("summary", String.format("Student %s dropped %s",
                enrolment.getStudentId(), enrolment.getCourseCode()));

        CampusEvent event = CampusEvent.of(
                RabbitMqConstants.ET_ENROLMENT_DROPPED,
                RabbitMqConstants.RK_ENROLMENT_DROPPED,
                "course-enrolment-service",
                data);
        eventPublisher.publishAsync(event);
    }

    private Map<String, Object> buildEnrolmentResponse(Enrolment enrolment, String warningMessage) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("enrolment", enrolment);
        if (warningMessage != null) {
            response.put("warning", warningMessage);
        }
        return response;
    }
}
