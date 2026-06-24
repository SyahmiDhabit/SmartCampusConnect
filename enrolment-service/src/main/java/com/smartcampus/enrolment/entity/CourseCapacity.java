package com.smartcampus.enrolment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "course_capacities")
public class CourseCapacity {

    @Id
    private String courseCode;
    private int maxCapacity;
    private int currentEnrolled;

    public CourseCapacity() {
    }

    public CourseCapacity(String courseCode, int maxCapacity, int currentEnrolled) {
        this.courseCode = courseCode;
        this.maxCapacity = maxCapacity;
        this.currentEnrolled = currentEnrolled;
    }

    // Getters and Setters
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentEnrolled() {
        return currentEnrolled;
    }

    public void setCurrentEnrolled(int currentEnrolled) {
        this.currentEnrolled = currentEnrolled;
    }

    public boolean hasCapacity() {
        return currentEnrolled < maxCapacity;
    }
}
