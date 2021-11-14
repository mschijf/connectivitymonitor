package com.ms.connectivitymonitor.commandline.ping;

import com.ms.connectivitymonitor.entity.PingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PingOutputParser {

    private static final Logger log = LoggerFactory.getLogger(PingOutputParser.class);

    public Optional<PingData> parsePingOutput(LocalDateTime startTime, String host, String pingOutput) {
        Pattern patternSummary = Pattern.compile("r.+ min/avg/max/.+dev = (.*?)/(.*?)/(.*?)/(.*?) ms\\n");
        Matcher matcherSummary = patternSummary.matcher(pingOutput);
        Pattern patternPackets = Pattern.compile("(.*?) packets transmitted, (.*?)[ packets]* received");
        Matcher matcherPackets = patternPackets.matcher(pingOutput);

        try {
            if (matcherSummary.find() && matcherSummary.groupCount() == 4 && matcherPackets.find() && matcherPackets.groupCount() == 2) {
                int min = parseToInteger(matcherSummary.group(1).trim());
                int avg = parseToInteger(matcherSummary.group(2).trim());
                int max = parseToInteger(matcherSummary.group(3).trim());
                int transmitted = parseToInteger(matcherPackets.group(1).trim());
                int received = parseToInteger(matcherPackets.group(2).trim());
                return Optional.of(new PingData(startTime, transmitted, received, min, avg, max, host));
            } else {
                log.error("Error while parsing {}", pingOutput);
                return Optional.empty();
            }
        } catch (NumberFormatException e) {
            log.error("Error while formatting to integer {}", pingOutput, e);
            return Optional.empty();
        }
    }

    private static Integer parseToInteger(String s) throws NumberFormatException {
        double d = Double.parseDouble(s);
        return (int) Math.round(d);
    }
}
