package com.ms.connectivitymonitor.commandline.ookla;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.connectivitymonitor.entity.SpeedtestData;
import com.ms.connectivitymonitor.commandline.ookla.responsemodel.OoklaResponseDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class OoklaJsonParser {
    private static final Logger log = LoggerFactory.getLogger(OoklaJsonParser.class);

    public Optional<SpeedtestData> ooklaOutputToSpeedTestData(String ooklaOutput) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            OoklaResponseDC response = mapper.readValue(ooklaOutput, OoklaResponseDC.class);
            return Optional.of(
                    new SpeedtestData(
                            LocalDateTime.now(),
                            response.getPing().getLatency(),
                            response.getPing().getJitter(),
                            response.getDownload().getBandwidth(),
                            response.getUpload().getBandwidth(),
                            response.getPacketLoss(),
                            response.getServer().getName(),
                            ooklaOutput)
                            );
        } catch (Exception exception) {
            if (ooklaOutput.contains("Resource temporarily unavailable")) {
                log.warn("Error while parsing ookla speedtest output {}", ooklaOutput);
            } else {
                log.error("Error while parsing ookla speedtest output {}", ooklaOutput);
            }
            return Optional.empty();
        }
    }

}
