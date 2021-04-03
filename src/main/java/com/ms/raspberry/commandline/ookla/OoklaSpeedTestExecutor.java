package com.ms.raspberry.commandline.ookla;

import com.ms.raspberry.entity.SpeedTestData;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface OoklaSpeedTestExecutor {
    Optional<SpeedTestData> execute();
}
