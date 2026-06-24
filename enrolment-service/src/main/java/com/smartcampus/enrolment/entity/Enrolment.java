package com.smartcampus.enrolment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "enrolments")
public class Enrolment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentId;
    private String courseCode;
    private String semester;
    private String status; // CONFIRMED, PROVISIONAL
    private long timestamp;

    public Enrolment() {
        this.timestamp = System.currentTimeMillis();
    }

    public Enrolment(String studentId, String courseCode, String semester, String status) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.semester = semester;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
