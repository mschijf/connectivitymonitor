package com.ms.raspberry.commandline.ping;

import com.ms.raspberry.commandline.CommandExecutor;
import com.ms.raspberry.entity.PingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PingExecutor {
    private static final Logger log = LoggerFactory.getLogger(PingExecutor.class);

    private final CommandExecutor commandExecutor;
    private final PingOutputParser pingOutputParser;

    @Autowired
    public PingExecutor(CommandExecutor commandExecutor, PingOutputParser pingOutputParser) {
        this.commandExecutor = commandExecutor;
        this.pingOutputParser = pingOutputParser;
    }

    public Optional<PingData> execute(String host, int count, int maxTimeSeconds) {
        String pingCommand = "ping " + host + " -c " + count + " -p " + maxTimeSeconds;
        try {
            LocalDateTime now = LocalDateTime.now();
            String outputLines = commandExecutor.execCommand(pingCommand);
            return pingOutputParser.parsePingOutput(now, host, outputLines);
        } catch (Exception exception) {
            log.error("Error while executing {}", pingCommand, exception);
            return Optional.empty();
        }
    }
}