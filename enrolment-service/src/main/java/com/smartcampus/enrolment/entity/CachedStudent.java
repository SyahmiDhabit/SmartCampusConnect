package com.smartcampus.enrolment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cached_students")
public class CachedStudent {

    @Id
    private String studentId;
    private String name;
    private String programme;
    private long lastCached;

    public CachedStudent() {
    }

    public CachedStudent(String studentId, String name, String programme) {
        this.studentId = studentId;
        this.name = name;
        this.programme = programme;
        this.lastCached = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public long getLastCached() {
        return lastCached;
    }

    public void setLastCached(long lastCached) {
        this.lastCached = lastCached;
    }
}
