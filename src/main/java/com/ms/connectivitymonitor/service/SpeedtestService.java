package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ookla.OoklaSpeedTestExecutor;
import com.ms.connectivitymonitor.entity.SpeedtestData;
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

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.max;

@Service
public class SpeedtestService {


    private static final Logger log = LoggerFactory.getLogger(SpeedtestService.class);
    private static final int MAX_RETRY_ATTEMPTS = 5;

    private final OoklaSpeedTestExecutor ooklaSpeedTestExecutor;
    private final double performanceDropRateTreshold;
    private final boolean enabled;
    private Lazy<AtomicInteger> gaugeDownloadSpeed;
    private Lazy<AtomicInteger> gaugeUploadSpeed;

    private Lazy<Counter> counterRetryNecessary;
    private Lazy<Counter> counterPerformanceDropped;
    private Lazy<Timer> jitterTimer;
    private Lazy<Timer> latencyTimer;

    private SpeedtestData lastSpeedtestResult = null;

    @Autowired
    public SpeedtestService(OoklaSpeedTestExecutor ooklaSpeedTestExecutor, MeterRegistry meterRegistry, Environment env) {
        this.ooklaSpeedTestExecutor = ooklaSpeedTestExecutor;
        this.performanceDropRateTreshold = Double.parseDouble(env.getProperty("spring.application.speedtest.performanceDropRateTreshold"));
        this.enabled = Boolean.parseBoolean(env.getProperty("spring.application.speedtest.enabled"));
        initMetrics(meterRegistry);
    }

    @Scheduled(cron = "${schedule.runspeedtest.cron:-}")
    private void scheduleFixedDelayTask() {
        if (enabled) {
            Instant start = Instant.now();
            doSpeedTest();
            log.debug("Run scheduled job in {}", Duration.between(start, Instant.now()).toMillis() / 1000.0);
        }
    }

    public Optional<SpeedtestData> doSpeedTest() {
        Optional<SpeedtestData> speedtestData = receiveSpeedtestData();
        speedtestData.ifPresentOrElse(this::processResult, this::processErrorResult);
        return speedtestData;
    }

    private void processErrorResult() {
        noMetrics();
        log.error("Speedtest not successfull");
    }

    private void processResult(SpeedtestData speedtestData) {
        lastSpeedtestResult = verifiedResult(speedtestData);
        setMetrics(lastSpeedtestResult, speedtestData);
        log.info("Speedtest successful. Host: {}, Download: {}, Upload: {}",
                lastSpeedtestResult.getServerName(), lastSpeedtestResult.getDownloadSpeedMbits(), lastSpeedtestResult.getUploadSpeedMbits());
    }

    private SpeedtestData verifiedResult(SpeedtestData speedtestData) {
        if (speedDropped(speedtestData)) {
            increaseMetricsPerformanceDrops();
            log.info("Drop in speed of {}%. Download: {}, upload: {}, server: {}.",
                    performanceDropRateTreshold *100, speedtestData.getDownloadSpeedMbits(), speedtestData.getUploadSpeedMbits(), speedtestData.getServerName());
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
            increaseMetricsRetries();
            log.info("Retry to fetch speedTest necessary. Retry attempt {} of {}", ++numberOfRetries, MAX_RETRY_ATTEMPTS);
            speedtestData = ooklaSpeedTestExecutor.execute();
        }
        return speedtestData;
    }

    //------------------------------------------------------------------------------------------------------------------

    private boolean speedDropped(SpeedtestData currentSpeedtestData) {
        if (lastSpeedtestResult == null) {
            return false;
        }
        return downloadSpeedDropped(currentSpeedtestData) || uploadSpeedDropped(currentSpeedtestData);
    }

    private boolean downloadSpeedDropped(SpeedtestData currentSpeedtestData) {
        int lastDownloadBytes = lastSpeedtestResult.getDownloadSpeedBytes();
        int currentDownloadBytes = currentSpeedtestData.getDownloadSpeedBytes();
        return (((double)currentDownloadBytes / (double)lastDownloadBytes) < (1- performanceDropRateTreshold));
    }

    private boolean uploadSpeedDropped(SpeedtestData currentSpeedtestData) {
        int lastUploadBytes = lastSpeedtestResult.getUploadSpeedBytes();
        int currentUploadBytes = currentSpeedtestData.getUploadSpeedBytes();
        return (((double)currentUploadBytes / (double)lastUploadBytes) < (1- performanceDropRateTreshold));
    }

    //------------------------------------------------------------------------------------------------------------------

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
        counterRetryNecessary = new Lazy<>() {
            @Override
            protected Counter init() {return meterRegistry.counter("retrySpeedTest");
            }
        };
        counterPerformanceDropped = new Lazy<>() {
            @Override
            protected Counter init() {return meterRegistry.counter("speedTestPerformanceDropped");
            }
        };
        jitterTimer = new Lazy<>() {
            @Override
            protected Timer init() {
                return Timer.builder("jittertime").register(meterRegistry);
            }
        };
        latencyTimer = new Lazy<>() {
            @Override
            protected Timer init() {
                return Timer.builder("latencytime").register(meterRegistry);
            }
        };
    }

    private void setMetrics(SpeedtestData lastTest, SpeedtestData backup) {
        gaugeDownloadSpeed.get().set(max(lastTest.getDownloadSpeedBytes(), backup.getDownloadSpeedBytes()));
        gaugeUploadSpeed.get().set(max(lastTest.getUploadSpeedBytes(), backup.getUploadSpeedBytes()));
        jitterTimer.get().record((int)(lastTest.getJitterMillis()*1000.0), TimeUnit.MICROSECONDS);
        latencyTimer.get().record((int)(lastTest.getLatencyMillis()*1000.0), TimeUnit.MICROSECONDS);
    }

    private void noMetrics() {
        gaugeDownloadSpeed.get().set(0);
        gaugeUploadSpeed.get().set(0);
    }

    private void increaseMetricsRetries() {
        counterRetryNecessary.get().increment(1);
    }

    private void increaseMetricsPerformanceDrops() {
        counterPerformanceDropped.get().increment(1);
    }
}

