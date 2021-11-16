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
    private final int MAX_RETRY_ATTEMPTS = 5;

    private final OoklaSpeedTestExecutor ooklaSpeedTestExecutor;
    private Lazy<AtomicInteger> gaugeDownloadSpeed;
    private Lazy<AtomicInteger> gaugeUploadSpeed;
    private Lazy<DistributionSummary> distibutionSummaryUpload;
    private Lazy<DistributionSummary> distibutionSummaryDownload;

    @Autowired
    public SpeedtestService(OoklaSpeedTestExecutor ooklaSpeedTestExecutor, MeterRegistry meterRegistry) {
        this.ooklaSpeedTestExecutor = ooklaSpeedTestExecutor;
        initMetrics(meterRegistry);
    }

    private void initMetrics(MeterRegistry meterRegistry) {
        gaugeUploadSpeed = new Lazy<AtomicInteger>() {
            @Override
            protected AtomicInteger init() {
                return meterRegistry.gauge("speedtest_speed_upload", new AtomicInteger(0));
            }
        };
        gaugeDownloadSpeed = new Lazy<AtomicInteger>() {
            @Override
            protected AtomicInteger init() {
                return meterRegistry.gauge("speedtest_speed_download", new AtomicInteger(0));
            }
        };
        distibutionSummaryUpload = new Lazy<DistributionSummary>() {
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
        distibutionSummaryDownload = new Lazy<DistributionSummary>() {
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
        Optional<SpeedtestData> speedTestData = receiveSpeedtestData();
        setMetrics(speedTestData);
        return speedTestData;
    }

    private Optional<SpeedtestData> receiveSpeedtestData() {
        Optional<SpeedtestData> speedTestData = ooklaSpeedTestExecutor.execute();
        int numberOfRetries = 0;
        while (speedTestData.isEmpty() && numberOfRetries < MAX_RETRY_ATTEMPTS) {
            numberOfRetries++;
            log.warn("Retry to fetch speedTest necessary. Retry attempt {} of {}", numberOfRetries, MAX_RETRY_ATTEMPTS);
            speedTestData = ooklaSpeedTestExecutor.execute();
        }
        return speedTestData;
    }

    private void setMetrics(Optional<SpeedtestData> speedTestData) {
        gaugeDownloadSpeed.get().set(speedTestData.map(SpeedtestData::getDownloadSpeedBytes).orElse(0));
        gaugeUploadSpeed.get().set(speedTestData.map(SpeedtestData::getUploadSpeedBytes).orElse(0));
        distibutionSummaryUpload.get().record(bytesToMBits(speedTestData.map(SpeedtestData::getUploadSpeedBytes).orElse(0)));
        distibutionSummaryDownload.get().record(bytesToMBits(speedTestData.map(SpeedtestData::getDownloadSpeedBytes).orElse(0)));
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

