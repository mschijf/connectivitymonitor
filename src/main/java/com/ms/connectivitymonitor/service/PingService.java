package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ping.PingExecutor;
import com.ms.connectivitymonitor.entity.PingData;
import com.ms.connectivitymonitor.entity.PingSummary;
import com.ms.connectivitymonitor.repository.PingQueryRepository;
import com.ms.connectivitymonitor.repository.PingRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PingService {

    PingRepository pingRepository;
    PingQueryRepository pingQueryRepository;
    PingExecutor pingExecutor;
    private AtomicInteger gaugePingMaxMs;
    private AtomicInteger gaugePingMinMs;
    private AtomicInteger gaugePingAvgMs;
    private AtomicInteger gaugePingPackageTransmitted;
    private AtomicInteger gaugePingPackageReceived;

    @Autowired
    public PingService(PingRepository pingRepository, PingQueryRepository pingQueryRepository, PingExecutor pingExecutor, MeterRegistry registry) {
        this.pingRepository = pingRepository;
        this.pingQueryRepository = pingQueryRepository;
        this.pingExecutor = pingExecutor;
        initMetrics(registry);
    }

    private void initMetrics(MeterRegistry registry) {
        gaugePingMaxMs = registry.gauge("ping.time.max", new AtomicInteger(0));
        gaugePingMinMs = registry.gauge("ping.time.min", new AtomicInteger(0));
        gaugePingAvgMs = registry.gauge("ping.time.avg", new AtomicInteger(0));
        gaugePingPackageTransmitted = registry.gauge("ping.package.transmitted", new AtomicInteger(0));
        gaugePingPackageReceived = registry.gauge("ping.package.received", new AtomicInteger(0));
    }

    public Optional<PingData> doPing() {
        Optional<PingData> pingData = pingExecutor.execute("kpn.nl", 50, 55);
        setMetrics(pingData);
        if (pingData.isEmpty()) {
            return pingData;
        }

        PingData savedData = pingRepository.save(pingData.get());
        return Optional.of(savedData);
    }

    private void setMetrics(Optional<PingData> pingData) {
        gaugePingAvgMs.set(pingData.map(PingData::getAvgTimeMillis).orElseGet(() -> 0));
        gaugePingMinMs.set(pingData.map(PingData::getMinTimeMillis).orElseGet(() -> 0));
        gaugePingMaxMs.set(pingData.map(PingData::getMaxTimeMillis).orElseGet(() -> 0));
        gaugePingPackageTransmitted.set(pingData.map(PingData::getPacketsTransmitted).orElseGet(() -> 0));
        gaugePingPackageReceived.set(pingData.map(PingData::getPacketsReceived).orElseGet(() -> 0));
    }

    public Optional<PingData> getPingData(Integer id) {
        return pingRepository.findById(id);
    }


    @Scheduled(cron = "${schedule.runping.cron:-}")
    public void scheduleFixedDelayTask() {
        doPing();
    }

    public Collection<PingSummary> getPingDaySummary() {
        return pingQueryRepository.getDaySummary();
    }

    public Collection<PingSummary> getPingHourSummary() {
        return pingQueryRepository.getHourSummary();
    }
    public Collection<PingSummary> getPingMinuteSummary() {
        return pingQueryRepository.getMinuteSummary();
    }

}

