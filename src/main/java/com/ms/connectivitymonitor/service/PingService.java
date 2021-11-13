package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ping.PingExecutor;
import com.ms.connectivitymonitor.entity.PingData;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PingService {

    PingExecutor pingExecutor;
    private Counter counterPingPackagesMissed;
    private Timer pingTimer;

    @Autowired
    public PingService(PingExecutor pingExecutor, MeterRegistry registry) {
        this.pingExecutor = pingExecutor;
        initMetrics(registry);
    }

    private void initMetrics(MeterRegistry registry) {
        counterPingPackagesMissed = registry.counter("pingpackagesMissed");
        pingTimer = Timer.builder("pingtime").register(registry);
    }

    public Optional<PingData> doPing() {
        int numberOfPings = 1;
        Optional<PingData> pingData = pingExecutor.execute("kpn.nl", numberOfPings, numberOfPings + 1);
        if (pingData.isPresent()) {
            counterPingPackagesMissed.increment(numberOfPings - pingData.get().getPacketsReceived());
            pingTimer.record(pingData.get().getMaxTimeMillis(), TimeUnit.MILLISECONDS);
        } else {
            counterPingPackagesMissed.increment(numberOfPings);
        }
        return pingData;
    }

    @Scheduled(fixedDelay = 500)
    public void scheduleFixedDelayTask() {
        doPing();
    }
}

