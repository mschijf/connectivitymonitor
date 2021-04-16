package com.ms.raspberry.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class PingSummary {
    @Id
    @Column(name = "date_time")
    private LocalDateTime fromDate;
    @Column(name = "total_transmitted")
    private Integer totalPacketsTransmitted;
    @Column(name = "total_received")
    private Integer totalPacketsReceived;
    @Column(name = "min_time_millis")
    private Integer minTimeMillis;
    @Column(name = "avg_time_millis")
    private Integer avgTimeMillis;
    @Column(name = "max_time_millis")
    private Integer maxTimeMillis;

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public Integer getTotalPacketsTransmitted() {
        return totalPacketsTransmitted;
    }

    public Integer getTotalPacketsReceived() {
        return totalPacketsReceived;
    }

    public Integer getMinTimeMillis() {
        return minTimeMillis;
    }

    public Integer getAvgTimeMillis() {
        return avgTimeMillis;
    }

    public Integer getMaxTimeMillis() {
        return maxTimeMillis;
    }
}
