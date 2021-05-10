package com.ms.raspberry.commandline.ookla;

import com.ms.raspberry.commandline.CommandExecutor;
import com.ms.raspberry.entity.SpeedtestData;
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

    private static final String CLI_COMMAND = "/usr/bin/speedtest  --progress=no --format=json";

    private CommandExecutor commandExecutor;
    private OoklaJsonParser ooklaOutputParser;

    @Autowired
    public OoklaSpeedTestExecutorImpl(CommandExecutor commandExecutor, OoklaJsonParser ooklaOutputParser) {
        this.commandExecutor = commandExecutor;
        this.ooklaOutputParser = ooklaOutputParser;
    }

    public Optional<SpeedtestData> execute() {
        try {
            String outputLines = commandExecutor.execCommand(CLI_COMMAND);
            return ooklaOutputParser.ooklaOutputToSpeedTestData(outputLines);
        } catch (Exception exception) {
            log.error("Error while executing {}", CLI_COMMAND, exception);
            return Optional.empty();
        }
    }
}