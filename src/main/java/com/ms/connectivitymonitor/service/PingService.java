package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ping.PingExecutor;
import com.ms.connectivitymonitor.entity.PingData;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PingService {

    PingExecutor pingExecutor;
    private Counter counterPingPackageTransmitted;
    private Counter counterPingPackageReceived;
    private Timer pingTimer;

    @Autowired
    public PingService(PingExecutor pingExecutor, MeterRegistry registry) {
        this.pingExecutor = pingExecutor;
        initMetrics(registry);
    }

    private void initMetrics(MeterRegistry registry) {
        counterPingPackageTransmitted = registry.counter("pingpackage", Collections.singletonList(new ImmutableTag("sendtype", "transmitted")));
        counterPingPackageReceived = registry.counter("pingpackage", Collections.singletonList(new ImmutableTag("sendtype", "received")));
        pingTimer = Timer.builder("pingtime").register(registry);
    }

    public Optional<PingData> doPing() {
        Optional<PingData> pingData = pingExecutor.execute("kpn.nl", 1, 2);
        setMetrics(pingData);
        return pingData;
    }

    private void setMetrics(Optional<PingData> pingData) {
        counterPingPackageTransmitted.increment(pingData.map(PingData::getPacketsTransmitted).orElse(0));
        counterPingPackageReceived.increment(pingData.map(PingData::getPacketsReceived).orElse(0));
        pingTimer.record(pingData.map(PingData::getMaxTimeMillis).orElse(0), TimeUnit.MILLISECONDS);
    }

//    @Scheduled(cron = "${schedule.runping.cron:-}")
    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        doPing();
    }
}

