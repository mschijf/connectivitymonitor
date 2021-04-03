package com.ms.raspberry.service;

import com.ms.raspberry.commandline.ping.PingExecutor;
import com.ms.raspberry.entity.PingData;
import com.ms.raspberry.repository.PingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PingService {

    private static final Logger log = LoggerFactory.getLogger(PingService.class);
    PingRepository repository;
    PingExecutor pingExecutor;

    @Autowired
    public PingService(PingRepository repository, PingExecutor pingExecutor) {
        this.repository = repository;
        this.pingExecutor = pingExecutor;
    }

    public Optional<PingData> doPing() {
        Optional<PingData> pingData = pingExecutor.execute("ziggo.nl", 50, 55);
        if (pingData.isEmpty())
            return pingData;

        PingData savedData = repository.save(pingData.get());
        return Optional.of(savedData);
    }

    public Optional<PingData> getPingData(Integer id) {
        return repository.findById(id);
    }


    @Scheduled(cron = "${schedule.runping.cron:-}")
    public void scheduleFixedDelayTask() {
        doPing();
    }
}

