package com.ms.raspberry.commandline.ookla;

import com.ms.raspberry.entity.SpeedtestData;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface OoklaSpeedTestExecutor {
    Optional<SpeedtestData> execute();
}
