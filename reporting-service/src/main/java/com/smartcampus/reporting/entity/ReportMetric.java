package com.smartcampus.reporting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "report_metrics")
public class ReportMetric {

    @Id
    private String metricKey;

    private long metricValue;

    public ReportMetric() {
    }

    public ReportMetric(String metricKey, long metricValue) {
        this.metricKey = metricKey;
        this.metricValue = metricValue;
    }

    public String getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(String metricKey) {
        this.metricKey = metricKey;
    }

    public long getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(long metricValue) {
        this.metricValue = metricValue;
    }
}
