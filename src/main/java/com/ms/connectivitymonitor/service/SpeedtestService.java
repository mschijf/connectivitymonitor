package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ookla.OoklaSpeedTestExecutor;
import com.ms.connectivitymonitor.entity.SpeedtestData;
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
    private final int MAX_RETRY_ATTEMPTS = 5;

    private static final Logger log = LoggerFactory.getLogger(SpeedtestService.class);
    private final OoklaSpeedTestExecutor ooklaSpeedTestExecutor;
    private AtomicInteger gaugeDownloadSpeed;
    private AtomicInteger gaugeUploadSpeed;
    DistributionSummary distibutionSummaryUpload;
    DistributionSummary distibutionSummaryDownload;

    @Autowired
    public SpeedtestService(OoklaSpeedTestExecutor ooklaSpeedTestExecutor, MeterRegistry registry) {
        this.ooklaSpeedTestExecutor = ooklaSpeedTestExecutor;
        initMetrics(registry);
    }

    private void initMetrics(MeterRegistry registry) {
        gaugeUploadSpeed = registry.gauge("speedtest_speed_upload", new AtomicInteger(0));
        gaugeDownloadSpeed = registry.gauge("speedtest_speed_download", new AtomicInteger(0));
        distibutionSummaryUpload = DistributionSummary
                .builder("internetspeed")
                .baseUnit("megabits/second")
                .tags("direction", "upload")
                .publishPercentiles(0.8, 0.9, 0.95)
                .publishPercentileHistogram()
                .register(registry);
        distibutionSummaryDownload = DistributionSummary
                .builder("internetspeed")
                .baseUnit("megabits/second")
                .tags("direction", "download")
                .publishPercentiles(0.8, 0.9, 0.95)
                .publishPercentileHistogram()
                .register(registry);
    }

    public Optional<SpeedtestData> doSpeedTest() {
        Optional<SpeedtestData> speedTestData = receiveSpeedtestData(MAX_RETRY_ATTEMPTS);
        setMetrics(speedTestData);
        return speedTestData;
    }

    private Optional<SpeedtestData> receiveSpeedtestData(int numberOfRetries) {
        Optional<SpeedtestData> speedTestData = ooklaSpeedTestExecutor.execute();
        while (speedTestData.isEmpty() && numberOfRetries > 0) {
            speedTestData = ooklaSpeedTestExecutor.execute();
            numberOfRetries--;
        }
        return speedTestData;
    }

    private void setMetrics(Optional<SpeedtestData> speedTestData) {
        gaugeDownloadSpeed.set(speedTestData.map(SpeedtestData::getDownloadSpeedBytes).orElse(0));
        gaugeUploadSpeed.set(speedTestData.map(SpeedtestData::getUploadSpeedBytes).orElse(0));
        distibutionSummaryUpload.record(bytesToMBits(speedTestData.map(SpeedtestData::getUploadSpeedBytes).orElse(0)));
        distibutionSummaryDownload.record(bytesToMBits(speedTestData.map(SpeedtestData::getDownloadSpeedBytes).orElse(0)));
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

