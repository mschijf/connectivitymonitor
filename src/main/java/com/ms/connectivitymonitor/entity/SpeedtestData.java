package com.ms.connectivitymonitor.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name="speedtest")
public class SpeedtestData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "speed_id_generator")
    @SequenceGenerator(name="speed_id_generator", sequenceName = "speed_id_seq", allocationSize = 1)
    private int id;

    @Column(name="run_date_time")
    private LocalDateTime runDateTime;

    @Column(name="latency_millis")
    private Double latencyMillis;

    @Column(name="jitter_millis")
    private Double jitterMillis;

    @Column(name="downloadspeed_bytes")
    private Integer downloadSpeedBytes;

    @Column(name="uploadspeed_bytes")
    private Integer uploadSpeedBytes;

    @Column(name="packet_loss_perc")
    private Double packetLoss;

    @Column(name="all_output")
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

    public Integer getUploadSpeedBytes() {
        return uploadSpeedBytes;
    }

    public Double getPacketLoss() {
        return packetLoss;
    }

    public String getAllOutput() {
        return allOutput;
    }
}
