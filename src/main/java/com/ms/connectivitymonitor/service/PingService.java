package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ping.PingExecutor;
import com.ms.connectivitymonitor.entity.PingData;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PingService {

    PingExecutor pingExecutor;
    private AtomicInteger gaugePingMaxMs;
    private AtomicInteger gaugePingMinMs;
    private AtomicInteger gaugePingAvgMs;
    private Counter counterPingPackageTransmitted;
    private Counter counterPingPackageReceived;
    private AtomicInteger gaugePingPackagesMissed;

    @Autowired
    public PingService(PingExecutor pingExecutor, MeterRegistry registry) {
        this.pingExecutor = pingExecutor;
        initMetrics(registry);
    }

    private void initMetrics(MeterRegistry registry) {
        gaugePingMaxMs = registry.gauge("pingtime", Collections.singletonList(new ImmutableTag("level", "max")), new AtomicInteger(0));
        gaugePingMinMs = registry.gauge("pingtime", Collections.singletonList(new ImmutableTag("level", "min")), new AtomicInteger(0));
        gaugePingAvgMs = registry.gauge("pingtime", Collections.singletonList(new ImmutableTag("level", "avg")), new AtomicInteger(0));
        counterPingPackageTransmitted = registry.counter("pingpackage", Collections.singletonList(new ImmutableTag("sendtype", "transmitted")));
        counterPingPackageReceived = registry.counter("pingpackage", Collections.singletonList(new ImmutableTag("sendtype", "received")));
        gaugePingPackagesMissed = registry.gauge("pingpackages_missed", new AtomicInteger(0));
    }

    public Optional<PingData> doPing() {
        Optional<PingData> pingData = pingExecutor.execute("kpn.nl", 50, 55);
        setMetrics(pingData);
        return pingData;
    }

    private void setMetrics(Optional<PingData> pingData) {
        gaugePingAvgMs.set(pingData.map(PingData::getAvgTimeMillis).orElse(0));
        gaugePingMinMs.set(pingData.map(PingData::getMinTimeMillis).orElse(0));
        gaugePingMaxMs.set(pingData.map(PingData::getMaxTimeMillis).orElse(0));
        int packagesTransmitted = pingData.map(PingData::getPacketsTransmitted).orElse(0);
        int packagesReceived = pingData.map(PingData::getPacketsReceived).orElse(0);

        counterPingPackageTransmitted.increment(packagesTransmitted);
        counterPingPackageReceived.increment(packagesReceived);
        gaugePingPackagesMissed.set(packagesTransmitted - packagesReceived);
    }

    @Scheduled(cron = "${schedule.runping.cron:-}")
    public void scheduleFixedDelayTask() {
        doPing();
    }
}

