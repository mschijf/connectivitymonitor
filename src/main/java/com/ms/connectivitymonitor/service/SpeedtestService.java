package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ookla.OoklaSpeedTestExecutor;
import com.ms.connectivitymonitor.entity.SpeedtestData;
import com.ms.connectivitymonitor.repository.SpeedtestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Service
public class SpeedtestService {

    private static final Logger log = LoggerFactory.getLogger(SpeedtestService.class);
    SpeedtestRepository repository;
    OoklaSpeedTestExecutor ooklaSpeedTestExecutor;

    @Autowired
    public SpeedtestService(SpeedtestRepository repository, OoklaSpeedTestExecutor ooklaSpeedTestExecutor) {
        this.repository = repository;
        this.ooklaSpeedTestExecutor = ooklaSpeedTestExecutor;
    }

    public Optional<SpeedtestData> doSpeedTest() {
        Optional<SpeedtestData> speedTestData = ooklaSpeedTestExecutor.execute();
        if (speedTestData.isEmpty())
            return speedTestData;

        SpeedtestData savedData = repository.save(speedTestData.get());
        return Optional.of(savedData);
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

