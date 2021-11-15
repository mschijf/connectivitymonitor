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
            int transmitted = 0;
            int received = 0;
            int min = 0;
            int avg = 0;
            int max = 0;
            if (matcherPackets.find() && matcherPackets.groupCount() == 2) {
                transmitted = parseToInteger(matcherPackets.group(1).trim());
                received = parseToInteger(matcherPackets.group(2).trim());
            } else {
                log.warn("Unexpected result while parsing {}", pingOutput);
                return Optional.empty();
            }
            if (matcherSummary.find() && matcherSummary.groupCount() == 4) {
                min = parseToInteger(matcherSummary.group(1).trim());
                avg = parseToInteger(matcherSummary.group(2).trim());
                max = parseToInteger(matcherSummary.group(3).trim());
            } else {
                log.warn("Unexpected result while parsing {}", pingOutput);
            }
            return Optional.of(new PingData(startTime, transmitted, received, min, avg, max, host));
        } catch (NumberFormatException e) {
            log.error("Cannot format to integer {}", pingOutput, e);
            return Optional.empty();
        }
    }

    private static Integer parseToInteger(String s) throws NumberFormatException {
        double d = Double.parseDouble(s);
        return (int) Math.round(d);
    }
}
