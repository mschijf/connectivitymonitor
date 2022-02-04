package com.ms.connectivitymonitor.commandline.ookla;

import com.ms.connectivitymonitor.commandline.CommandExecutor;
import com.ms.connectivitymonitor.entity.SpeedtestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Profile("prod")
@Component
public class OoklaSpeedTestExecutorImpl implements OoklaSpeedTestExecutor {
    private static final Logger log = LoggerFactory.getLogger(OoklaSpeedTestExecutorImpl.class);

    private static final String CLI_COMMAND_GDPR = "/usr/bin/speedtest --accept-gdpr --progress=no --format=json";

    private CommandExecutor commandExecutor;
    private OoklaJsonParser ooklaOutputParser;

    @Autowired
    public OoklaSpeedTestExecutorImpl(CommandExecutor commandExecutor, OoklaJsonParser ooklaOutputParser) {
        this.commandExecutor = commandExecutor;
        this.ooklaOutputParser = ooklaOutputParser;
    }

    public Optional<SpeedtestData> execute() {
        try {
            String outputLines = commandExecutor.execCommand(CLI_COMMAND_GDPR);
            return ooklaOutputParser.ooklaOutputToSpeedTestData(outputLines);
        } catch (Exception exception) {
            log.warn("Error while executing {}", CLI_COMMAND_GDPR, exception);
            return Optional.empty();
        }
    }
}