package com.ms.raspberry.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="ping")
@Entity(name="ping")
public class PingData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ping_id_generator")
    @SequenceGenerator(name="ping_id_generator", sequenceName = "ping_id_seq", allocationSize = 1)
    private int id;

    @Column(name="run_date_time")
    private LocalDateTime runDateTime;
    @Column(name="packets_transmitted")
    private Integer packetsTransmitted;
    @Column(name="packets_received")
    private Integer packetsReceived;
    @Column(name="mintime_millis")
    private Integer minTimeMillis;
    @Column(name="avgtime_millis")
    private Integer avgTimeMillis;
    @Column(name="maxtime_millis")
    private Integer maxTimeMillis;
    @Column(name="host")
    private String host;

    public PingData() {}

    public PingData(LocalDateTime runDateTime, Integer packetsTransmitted, Integer packetsReceived, Integer minTimeMillis, Integer avgTimeMillis, Integer maxTimeMillis, String host) {
        this.runDateTime = runDateTime;
        this.packetsTransmitted = packetsTransmitted;
        this.packetsReceived = packetsReceived;
        this.minTimeMillis = minTimeMillis;
        this.avgTimeMillis = avgTimeMillis;
        this.maxTimeMillis = maxTimeMillis;
        this.host = host;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getRunDateTime() {
        return runDateTime;
    }

    public void setRunDateTime(LocalDateTime runDateTime) {
        this.runDateTime = runDateTime;
    }

    public Integer getPacketsTransmitted() {
        return packetsTransmitted;
    }

    public void setPacketsTransmitted(Integer packetsTransmitted) {
        this.packetsTransmitted = packetsTransmitted;
    }

    public Integer getPacketsReceived() {
        return packetsReceived;
    }

    public void setPacketsReceived(Integer packetsReceived) {
        this.packetsReceived = packetsReceived;
    }

    public Integer getMinTimeMillis() {
        return minTimeMillis;
    }

    public void setMinTimeMillis(Integer minTimeMillis) {
        this.minTimeMillis = minTimeMillis;
    }

    public Integer getAvgTimeMillis() {
        return avgTimeMillis;
    }

    public void setAvgTimeMillis(Integer avgTimeMillis) {
        this.avgTimeMillis = avgTimeMillis;
    }

    public Integer getMaxTimeMillis() {
        return maxTimeMillis;
    }

    public void setMaxTimeMillis(Integer maxTimeMillis) {
        this.maxTimeMillis = maxTimeMillis;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}