package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ookla.OoklaSpeedTestExecutor;
import com.ms.connectivitymonitor.entity.SpeedtestData;
import com.ms.tools.Lazy;
import io.micrometer.core.instrument.DistributionSummary;
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
    private static final double DECREASE_RATE = 0.8;

    private final OoklaSpeedTestExecutor ooklaSpeedTestExecutor;
    private Lazy<AtomicInteger> gaugeDownloadSpeed;
    private Lazy<AtomicInteger> gaugeUploadSpeed;
    private Lazy<DistributionSummary> distibutionSummaryUpload;
    private Lazy<DistributionSummary> distibutionSummaryDownload;
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
        distibutionSummaryUpload = new Lazy<>() {
            @Override
            protected DistributionSummary init() {
                return DistributionSummary
                        .builder("internetspeed")
                        .baseUnit("megabits/second")
                        .tags("direction", "upload")
                        .publishPercentiles(0.8, 0.9, 0.95)
                        .publishPercentileHistogram()
                        .register(meterRegistry);
            }
        };
        distibutionSummaryDownload = new Lazy<>() {
            @Override
            protected DistributionSummary init() {
                return DistributionSummary
                        .builder("internetspeed")
                        .baseUnit("megabits/second")
                        .tags("direction", "download")
                        .publishPercentiles(0.8, 0.9, 0.95)
                        .publishPercentileHistogram()
                        .register(meterRegistry);
            }
        };
    }

    public Optional<SpeedtestData> doSpeedTest() {
        Optional<SpeedtestData> speedtestData = receiveSpeedtestData();
        if (speedtestData.isPresent()) {
            lastSpeedtestResult = verifiedResult(speedtestData.get());
            setMetrics(lastSpeedtestResult);
            return Optional.of(lastSpeedtestResult);
        }
        return speedtestData;
    }

    private SpeedtestData verifiedResult(SpeedtestData speedtestData) {
        if (recheckSpeedtestDataNecessary(speedtestData)) {
            log.warn("Drop of {}% in downloadspeed. Let's do an extra check", DECREASE_RATE*100);
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
        int lastDownloadBytes = lastSpeedtestResult.getDownloadSpeedBytes();
        int currentDownloadBytes = currentSpeedtestData.getDownloadSpeedBytes();
        return ((double)currentDownloadBytes / (double)lastDownloadBytes) < DECREASE_RATE;
    }


    private void setMetrics(SpeedtestData speedTestData) {
        gaugeDownloadSpeed.get().set(speedTestData.getDownloadSpeedBytes());
        gaugeUploadSpeed.get().set(speedTestData.getUploadSpeedBytes());
        distibutionSummaryUpload.get().record(bytesToMBits(speedTestData.getUploadSpeedBytes()));
        distibutionSummaryDownload.get().record(bytesToMBits(speedTestData.getDownloadSpeedBytes()));
    }

    private int bytesToMBits(int nBytes) {
        return nBytes * 8 / 1000000;
    }

    @Scheduled(cron = "${schedule.runspeedtest.cron:-}")
    public void scheduleFixedDelayTask() {
        Instant start = Instant.now();
        doSpeedTest();
        log.debug("Run scheduled job in {}", Duration.between(start, Instant.now()).toMillis()/1000.0);
    }
}

