package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ping.PingExecutor;
import com.ms.connectivitymonitor.entity.PingData;
import com.ms.tools.Lazy;
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

    private PingExecutor pingExecutor;
    private Lazy<Counter> counterPingPackagesMissed;
    private Lazy<Timer> pingTimer;

    @Autowired
    public PingService(PingExecutor pingExecutor, MeterRegistry meterRegistry) {
        this.pingExecutor = pingExecutor;
        initMetrics(meterRegistry);
    }

    public Optional<PingData> doPing() {
        int numberOfPings = 1;
        Optional<PingData> pingData = pingExecutor.execute("kpn.nl", numberOfPings, numberOfPings + 1);
        setMetrics(pingData, numberOfPings);
        return pingData;
    }

    private void initMetrics(MeterRegistry meterRegistry) {
        counterPingPackagesMissed = new Lazy<Counter>() {
            @Override
            protected Counter init() {
                return meterRegistry.counter("pingpackagesMissed");
            }
        };
        pingTimer = new Lazy<Timer>() {
            @Override
            protected Timer init() {
                return Timer.builder("pingtime").register(meterRegistry);
            }
        };
    }

    private void setMetrics(Optional<PingData> pingData, int numberOfPings) {
        if (pingData.isPresent()) {
            counterPingPackagesMissed.get().increment(pingData.get().getPacketsTransmitted() - pingData.get().getPacketsReceived());
            pingTimer.get().record(pingData.get().getMaxTimeMillis(), TimeUnit.MILLISECONDS);
        } else {
            counterPingPackagesMissed.get().increment(numberOfPings);
        }
    }

    @Scheduled(fixedDelay = 500)
    public void scheduleFixedDelayTask() {
        doPing();
    }
}

