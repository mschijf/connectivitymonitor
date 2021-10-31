package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ookla.OoklaSpeedTestExecutor;
import com.ms.connectivitymonitor.entity.SpeedtestData;
import com.ms.connectivitymonitor.repository.SpeedtestRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SpeedtestService {

    private static final Logger log = LoggerFactory.getLogger(SpeedtestService.class);
    private SpeedtestRepository repository;
    private OoklaSpeedTestExecutor ooklaSpeedTestExecutor;
    private AtomicInteger gaugeDownloadSpeed;
    private AtomicInteger gaugeUploadSpeed;


    @Autowired
    public SpeedtestService(SpeedtestRepository repository, OoklaSpeedTestExecutor ooklaSpeedTestExecutor, MeterRegistry registry) {
        this.repository = repository;
        this.ooklaSpeedTestExecutor = ooklaSpeedTestExecutor;
        initMetrics(registry);
    }

    private void initMetrics(MeterRegistry registry) {
        gaugeDownloadSpeed = registry.gauge("speedtest.speed.download", new AtomicInteger(0));
        gaugeUploadSpeed = registry.gauge("speedtest.speed.upload", new AtomicInteger(0));
    }

    public Optional<SpeedtestData> doSpeedTest() {
        Optional<SpeedtestData> speedTestData = ooklaSpeedTestExecutor.execute();
        setMetrics(speedTestData);
        if (speedTestData.isEmpty()) {
            return speedTestData;
        }
        SpeedtestData savedData = repository.save(speedTestData.get());
        return Optional.of(savedData);
    }

    private void setMetrics(Optional<SpeedtestData> speedTestData) {
        gaugeDownloadSpeed.set(speedTestData.map(SpeedtestData::getDownloadSpeedBytes).orElseGet(() -> 0));
        gaugeUploadSpeed.set(speedTestData.map(SpeedtestData::getUploadSpeedBytes).orElseGet(() -> 0));
    }

    public Optional<SpeedtestData> getSpeedTestData(Integer id) {
        return repository.findById(id);
    }

    public Collection<SpeedtestData> getSpeedPerHour() {
        return repository.getHourResults();
    }


    @Scheduled(cron = "${schedule.runspeedtest.cron:-}")
    public void scheduleFixedDelayTask() {
        Instant start = Instant.now();
        doSpeedTest();
        log.info("Run scheduled job in {}", Duration.between(start, Instant.now()).toMillis()/1000.0);
    }
}

