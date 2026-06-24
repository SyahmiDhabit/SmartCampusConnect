package com.smartcampus.student.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {

    @Id
    private String id; // Student ID (e.g. S1001, S1002)
    private String name;
    private String email;
    private String programme;
    private double gpa;

    public Student() {
    }

    public Student(String id, String name, String email, String programme, double gpa) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.programme = programme;
        this.gpa = gpa;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }
}
