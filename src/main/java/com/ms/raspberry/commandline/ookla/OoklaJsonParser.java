package com.ms.raspberry.commandline.ookla;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.raspberry.entity.SpeedtestData;
import com.ms.raspberry.commandline.ookla.responsemodel.OoklaResponseDC;
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
                            ooklaOutput)
                            );
        } catch (Exception exception) {
            log.error("Error while parsing ookla speedtest output {}", ooklaOutput);
            return Optional.of(new SpeedtestData(LocalDateTime.now(), ooklaOutput));
        }
    }

}
