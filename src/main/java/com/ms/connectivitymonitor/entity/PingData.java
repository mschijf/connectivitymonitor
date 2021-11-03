package com.ms.connectivitymonitor.entity;

import java.time.LocalDateTime;

public class PingData {
    private int id;
    private LocalDateTime runDateTime;
    private Integer packetsTransmitted;
    private Integer packetsReceived;
    private Integer minTimeMillis;
    private Integer avgTimeMillis;
    private Integer maxTimeMillis;
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

    public LocalDateTime getRunDateTime() {
        return runDateTime;
    }

    public Integer getPacketsTransmitted() {
        return packetsTransmitted;
    }

    public Integer getPacketsReceived() {
        return packetsReceived;
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

    public String getHost() {
        return host;
    }
}