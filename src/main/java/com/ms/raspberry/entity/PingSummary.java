package com.ms.raspberry.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class PingSummary {
    @Id
    @Column(name = "date_time_hour")
    private LocalDateTime fromDate;
    @Column(name = "total_transmitted")
    private int totalPacketsTransmitted;
    @Column(name = "total_received")
    private int totalPacketsReceived;
    @Column(name = "min_time_millis")
    private int minTimeMillis;
    @Column(name = "avg_time_millis")
    private int avgTimeMillis;
    @Column(name = "max_time_millis")
    private int maxTimeMillis;


    public PingSummary() {

    }


    public PingSummary(LocalDateTime fromDate, int totalPacketsTransmitted, int totalPacketsReceived, int minTimeMillis, int avgTimeMillis, int maxTimeMillis) {
        this.fromDate = fromDate;
        this.totalPacketsTransmitted = totalPacketsTransmitted;
        this.totalPacketsReceived = totalPacketsReceived;
        this.minTimeMillis = minTimeMillis;
        this.avgTimeMillis = avgTimeMillis;
        this.maxTimeMillis = maxTimeMillis;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public int getTotalPacketsTransmitted() {
        return totalPacketsTransmitted;
    }

    public int getTotalPacketsReceived() {
        return totalPacketsReceived;
    }

    public int getMinTimeMillis() {
        return minTimeMillis;
    }

    public int getAvgTimeMillis() {
        return avgTimeMillis;
    }

    public int getMaxTimeMillis() {
        return maxTimeMillis;
    }
}
