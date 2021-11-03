package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ookla.OoklaSpeedTestExecutor;
import com.ms.connectivitymonitor.entity.SpeedtestData;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SpeedtestService {

    private static final Logger log = LoggerFactory.getLogger(SpeedtestService.class);
    private OoklaSpeedTestExecutor ooklaSpeedTestExecutor;
    private AtomicInteger gaugeDownloadSpeed;
    private AtomicInteger gaugeUploadSpeed;


    @Autowired
    public SpeedtestService(OoklaSpeedTestExecutor ooklaSpeedTestExecutor, MeterRegistry registry) {
        this.ooklaSpeedTestExecutor = ooklaSpeedTestExecutor;
        initMetrics(registry);
    }

    private void initMetrics(MeterRegistry registry) {
        gaugeUploadSpeed = registry.gauge("speedtest_speed_upload", new AtomicInteger(0));
        gaugeDownloadSpeed = registry.gauge("speedtest_speed_download", new AtomicInteger(0));
    }

    public Optional<SpeedtestData> doSpeedTest() {
        Optional<SpeedtestData> speedTestData = ooklaSpeedTestExecutor.execute();
        setMetrics(speedTestData);
        return speedTestData;
    }

    private void setMetrics(Optional<SpeedtestData> speedTestData) {
        gaugeDownloadSpeed.set(speedTestData.map(SpeedtestData::getDownloadSpeedBytes).orElse(0));
        gaugeUploadSpeed.set(speedTestData.map(SpeedtestData::getUploadSpeedBytes).orElse(0));
    }

    @Scheduled(cron = "${schedule.runspeedtest.cron:-}")
    public void scheduleFixedDelayTask() {
        Instant start = Instant.now();
        doSpeedTest();
        log.debug("Run scheduled job in {}", Duration.between(start, Instant.now()).toMillis()/1000.0);
    }
}

