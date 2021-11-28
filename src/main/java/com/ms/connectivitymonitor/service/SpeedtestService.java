package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ookla.OoklaSpeedTestExecutor;
import com.ms.connectivitymonitor.entity.SpeedtestData;
import com.ms.tools.Lazy;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SpeedtestService {
    private static final Logger log = LoggerFactory.getLogger(SpeedtestService.class);
    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final double DECREASE_RATE = 0.85;

    private final OoklaSpeedTestExecutor ooklaSpeedTestExecutor;
    private Lazy<AtomicInteger> gaugeDownloadSpeed;
    private Lazy<AtomicInteger> gaugeUploadSpeed;
    private SpeedtestData lastSpeedtestResult = null;

    @Autowired
    public SpeedtestService(OoklaSpeedTestExecutor ooklaSpeedTestExecutor, MeterRegistry meterRegistry) {
        this.ooklaSpeedTestExecutor = ooklaSpeedTestExecutor;
        initMetrics(meterRegistry);
    }

    private void initMetrics(MeterRegistry meterRegistry) {
        gaugeUploadSpeed = new Lazy<>() {
            @Override
            protected AtomicInteger init() {
                return meterRegistry.gauge("speedtest_speed_upload", new AtomicInteger(0));
            }
        };
        gaugeDownloadSpeed = new Lazy<>() {
            @Override
            protected AtomicInteger init() {
                return meterRegistry.gauge("speedtest_speed_download", new AtomicInteger(0));
            }
        };
    }

    public Optional<SpeedtestData> doSpeedTest() {
        Optional<SpeedtestData> speedtestData = receiveSpeedtestData();
        if (speedtestData.isPresent()) {
            lastSpeedtestResult = verifiedResult(speedtestData.get());
            setMetrics(lastSpeedtestResult);
            log.info("Speedtest successful. Host: {}, Downloadspeed: {}, Uploadspeed: {}",
                    lastSpeedtestResult.getServerName(), lastSpeedtestResult.getDownloadSpeedMbits(), lastSpeedtestResult.getUploadSpeedMbits());
            return Optional.of(lastSpeedtestResult);
        } else {
            log.error("Speedtest not successfull");
        }
        return speedtestData;
    }

    private SpeedtestData verifiedResult(SpeedtestData speedtestData) {
        if (recheckSpeedtestDataNecessary(speedtestData)) {
            log.warn("Drop in speed of {}%. Downloadspeed = {}, uploadspeed = {}, server: {}.  Let's do an extra check",
                    DECREASE_RATE*100, speedtestData.getDownloadSpeedMbits(), speedtestData.getUploadSpeedMbits(), speedtestData.getServerName());
            Optional<SpeedtestData> speedtestDataRetry = receiveSpeedtestData();
            return speedtestDataRetry.orElse(speedtestData);
        } else {
            return speedtestData;
        }
    }

    private Optional<SpeedtestData> receiveSpeedtestData() {
        Optional<SpeedtestData> speedtestData = ooklaSpeedTestExecutor.execute();
        int numberOfRetries = 0;
        while (speedtestData.isEmpty() && numberOfRetries < MAX_RETRY_ATTEMPTS) {
            numberOfRetries++;
            log.warn("Retry to fetch speedTest necessary. Retry attempt {} of {}", numberOfRetries, MAX_RETRY_ATTEMPTS);
            speedtestData = ooklaSpeedTestExecutor.execute();
        }
        return speedtestData;
    }


    private boolean recheckSpeedtestDataNecessary(SpeedtestData currentSpeedtestData) {
        if (lastSpeedtestResult == null) {
            return false;
        }
        return downloadSpeedDropped(currentSpeedtestData) || uploadSpeedDropped(currentSpeedtestData);
    }

    private boolean downloadSpeedDropped(SpeedtestData currentSpeedtestData) {
        int lastDownloadBytes = lastSpeedtestResult.getDownloadSpeedBytes();
        int currentDownloadBytes = currentSpeedtestData.getDownloadSpeedBytes();
        return (((double)currentDownloadBytes / (double)lastDownloadBytes) < DECREASE_RATE);
    }

    private boolean uploadSpeedDropped(SpeedtestData currentSpeedtestData) {
        int lastUploadBytes = lastSpeedtestResult.getUploadSpeedBytes();
        int currentUploadBytes = currentSpeedtestData.getUploadSpeedBytes();
        return (((double)currentUploadBytes / (double)lastUploadBytes) < DECREASE_RATE);
    }

    private void setMetrics(SpeedtestData speedTestData) {
        gaugeDownloadSpeed.get().set(speedTestData.getDownloadSpeedBytes());
        gaugeUploadSpeed.get().set(speedTestData.getUploadSpeedBytes());
    }


    @Scheduled(cron = "${schedule.runspeedtest.cron:-}")
    public void scheduleFixedDelayTask() {
        Instant start = Instant.now();
        doSpeedTest();
        log.info("Run scheduled job in {}", Duration.between(start, Instant.now()).toMillis()/1000.0);
    }
}

