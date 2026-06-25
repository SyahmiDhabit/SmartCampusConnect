package com.smartcampus.reporting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "programme_enrolment_stats")
public class ProgrammeEnrolmentStat {

    @Id
    private String programme;

    private int enrolmentCount;
    private int confirmedCount;
    private int provisionalCount;

    public ProgrammeEnrolmentStat() {
    }

    public ProgrammeEnrolmentStat(String programme) {
        this.programme = programme;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public int getEnrolmentCount() {
        return enrolmentCount;
    }

    public void setEnrolmentCount(int enrolmentCount) {
        this.enrolmentCount = enrolmentCount;
    }

    public int getConfirmedCount() {
        return confirmedCount;
    }

    public void setConfirmedCount(int confirmedCount) {
        this.confirmedCount = confirmedCount;
    }

    public int getProvisionalCount() {
        return provisionalCount;
    }

    public void setProvisionalCount(int provisionalCount) {
        this.provisionalCount = provisionalCount;
    }
}
