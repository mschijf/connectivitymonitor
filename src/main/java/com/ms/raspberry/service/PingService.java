package com.ms.raspberry.service;

import com.ms.raspberry.commandline.ping.PingExecutor;
import com.ms.raspberry.entity.PingData;
import com.ms.raspberry.entity.PingSummary;
import com.ms.raspberry.repository.PingRepository;
import com.ms.raspberry.repository.PingQueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Service
public class PingService {

    private static final Logger log = LoggerFactory.getLogger(PingService.class);

    PingRepository pingRepository;
    PingQueryRepository pingQueryRepository;
    PingExecutor pingExecutor;

    @Autowired
    public PingService(PingRepository pingRepository, PingQueryRepository pingQueryRepository, PingExecutor pingExecutor) {
        this.pingRepository = pingRepository;
        this.pingQueryRepository = pingQueryRepository;
        this.pingExecutor = pingExecutor;
    }

    public Optional<PingData> doPing() {
        Optional<PingData> pingData = pingExecutor.execute("ziggo.nl", 50, 55);
        if (pingData.isEmpty())
            return pingData;

        PingData savedData = pingRepository.save(pingData.get());
        return Optional.of(savedData);
    }

    public Optional<PingData> getPingData(Integer id) {
        return pingRepository.findById(id);
    }


    @Scheduled(cron = "${schedule.runping.cron:-}")
    public void scheduleFixedDelayTask() {
        doPing();
    }

    public Collection<PingSummary> getPingSummary(LocalDate day) {
        return day != null ? pingQueryRepository.getDaySummary(day) : pingQueryRepository.getDaySummary();
    }
}

