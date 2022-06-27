package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ping.PingExecutor;
import com.ms.connectivitymonitor.entity.PingData;
import com.ms.tools.Lazy;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PingService {
    private static final Logger log = LoggerFactory.getLogger(PingService.class);

    private final PingExecutor pingExecutor;
    private Lazy<Counter> counterPingPackagesMissed;
    private Lazy<Timer> pingTimer;
    private int countExecuted = 0;
    private final String pingHost;
    private final boolean enabled;


    @Autowired
    public PingService(PingExecutor pingExecutor, MeterRegistry meterRegistry, Environment env) {
        this.pingExecutor = pingExecutor;
        this.pingHost = env.getProperty("spring.application.pingtest.pingHost");
        this.enabled = Boolean.parseBoolean(env.getProperty("spring.application.pingtest.enabled"));

        initMetrics(meterRegistry);
    }

    @Scheduled(fixedDelay = 500)
    private void scheduleFixedDelayTask() {
        if (enabled) {
            doPing();
        }
    }

    public Optional<PingData> doPing() {
        int numberOfPings = 1;
        Optional<PingData> pingData = pingExecutor.execute(pingHost, numberOfPings, numberOfPings + 1);
        pingData.ifPresentOrElse(this::setMetrics, ()->setMetricsWhenMissingData(numberOfPings));

        if (++countExecuted % 500 == 0) {
            if (pingData.isPresent()) {
                log.info("Run another 500 pings. Last ping: {}ms ", pingData.get().getMaxTimeMillis());
            } else {
                log.info("Run another 500 pings. Last ping not succesfull");
            }
        }

        return pingData;
    }

    private void initMetrics(MeterRegistry meterRegistry) {
        counterPingPackagesMissed = new Lazy<>() {
            @Override
            protected Counter init() {return meterRegistry.counter("pingpackagesMissed");
            }
        };
        pingTimer = new Lazy<>() {
            @Override
            protected Timer init() {
                return Timer.builder("pingtime").register(meterRegistry);
            }
        };
    }

    private void setMetrics(PingData pingData) {
        counterPingPackagesMissed.get().increment(pingData.getPacketsTransmitted() - pingData.getPacketsReceived());
        pingTimer.get().record(pingData.getMaxTimeMillis(), TimeUnit.MILLISECONDS);
    }

    private void setMetricsWhenMissingData(int numberOfPings) {
        counterPingPackagesMissed.get().increment(numberOfPings);
    }
}

