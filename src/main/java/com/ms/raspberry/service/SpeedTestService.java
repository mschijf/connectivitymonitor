package com.ms.raspberry.service;

import com.ms.raspberry.commandline.ookla.OoklaSpeedTestExecutor;
import com.ms.raspberry.entity.SpeedTestData;
import com.ms.raspberry.repository.SpeedTestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class SpeedTestService {

    private static final Logger log = LoggerFactory.getLogger(SpeedTestService.class);
    SpeedTestRepository repository;
    OoklaSpeedTestExecutor ooklaSpeedTestExecutor;

    @Autowired
    public SpeedTestService(SpeedTestRepository repository, OoklaSpeedTestExecutor ooklaSpeedTestExecutor) {
        this.repository = repository;
        this.ooklaSpeedTestExecutor = ooklaSpeedTestExecutor;
    }

    public Optional<SpeedTestData> doSpeedTest() {
        Optional<SpeedTestData> speedTestData = ooklaSpeedTestExecutor.execute();
        if (speedTestData.isEmpty())
            return speedTestData;

        SpeedTestData savedData = repository.save(speedTestData.get());
        return Optional.of(savedData);
    }

    public Optional<SpeedTestData> getSpeedTestData(Integer id) {
        return repository.findById(id);
    }


    @Scheduled(cron = "${schedule.runspeedtest.cron:-}")
    public void scheduleFixedDelayTask() {
        Instant start = Instant.now();
        doSpeedTest();
        log.info("Run scheduled job in {}", Duration.between(start, Instant.now()).toMillis()/1000.0);
    }
}

