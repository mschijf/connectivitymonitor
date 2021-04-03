package com.ms.raspberry.commandline.ookla.responsemodel;

public class OoklaPingDC {
    Double jitter;
    Double latency;

    public Double getJitter() {
        return jitter;
    }

    public void setJitter(Double jitter) {
        this.jitter = jitter;
    }

    public Double getLatency() {
        return latency;
    }

    public void setLatency(Double latency) {
        this.latency = latency;
    }
}
