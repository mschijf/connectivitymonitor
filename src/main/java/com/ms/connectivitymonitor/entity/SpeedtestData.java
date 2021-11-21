package com.ms.connectivitymonitor.entity;

import java.time.LocalDateTime;

public class SpeedtestData {
    private int id;
    private LocalDateTime runDateTime;
    private Double latencyMillis;
    private Double jitterMillis;
    private Integer downloadSpeedBytes;
    private Integer uploadSpeedBytes;
    private Double packetLoss;
    private String allOutput;

    public SpeedtestData() {
    }

    public SpeedtestData(LocalDateTime runDateTime, String allOutput) {
        this.runDateTime = runDateTime;
        this.allOutput = allOutput.trim();
        this.latencyMillis = null;
        this.downloadSpeedBytes = null;
        this.uploadSpeedBytes = null;
        this.packetLoss = null;
        this.allOutput = trimAllOutput(allOutput);
    }

    public SpeedtestData(LocalDateTime runDateTime, Double latencyMillis, Double jitterMillis, Integer downloadSpeedBytes, Integer uploadSpeedBytes, Double packetLoss, String allOutput) {
        this.runDateTime = runDateTime;
        this.latencyMillis = latencyMillis;
        this.jitterMillis = jitterMillis;
        this.downloadSpeedBytes = downloadSpeedBytes;
        this.uploadSpeedBytes = uploadSpeedBytes;
        this.packetLoss = packetLoss;
        this.allOutput = trimAllOutput(allOutput);
    }

    private String trimAllOutput(final String trimAllOutput) {
        String s = trimAllOutput.strip();
        return (s.length() > 2048) ? s.substring(0,2048) : s;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getRunDateTime() {
        return runDateTime;
    }

    public Double getLatencyMillis() {
        return latencyMillis;
    }

    public Double getJitterMillis() {
        return jitterMillis;
    }

    public Integer getDownloadSpeedBytes() {
        return downloadSpeedBytes;
    }

    public Integer getDownloadSpeedMbits() {
        return downloadSpeedBytes == null ? null : bytesToMBits(downloadSpeedBytes);
    }

    public Integer getUploadSpeedBytes() {
        return uploadSpeedBytes;
    }

    public Integer getUploadSpeedMbits() {
        return uploadSpeedBytes == null ? null : bytesToMBits(uploadSpeedBytes);
    }

    public Double getPacketLoss() {
        return packetLoss;
    }

    public String getAllOutput() {
        return allOutput;
    }

    private int bytesToMBits(int nBytes) {
        return nBytes * 8 / 1000000;
    }

}
