package com.ms.raspberry.commandline.ping;

import com.ms.raspberry.entity.PingData;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PingOutputParser {

    public Optional<PingData> parsePingOutput(LocalDateTime startTime, String host, String pingOutput) {
        Pattern patternSummary = Pattern.compile("r.+ min/avg/max/.+dev = (.*?)/(.*?)/(.*?)/(.*?) ms\\n");
        Matcher matcherSummary = patternSummary.matcher(pingOutput);
        Pattern patternPackets = Pattern.compile("(.*?) packets transmitted, (.*?)[ packets]* received");
        Matcher matcherPackets = patternPackets.matcher(pingOutput);

        Integer min  = null;
        Integer avg  = null;
        Integer max  = null;
        Integer transmitted = null;
        Integer received = null;
        if (matcherSummary.find() && matcherSummary.groupCount() == 4) {
            min = parseToIntegerOrNull(matcherSummary.group(1).trim());
            avg = parseToIntegerOrNull(matcherSummary.group(2).trim());
            max = parseToIntegerOrNull(matcherSummary.group(3).trim());
        }
        if (matcherPackets.find() && matcherPackets.groupCount() == 2) {
            transmitted = parseToIntegerOrNull(matcherPackets.group(1).trim());
            received = parseToIntegerOrNull(matcherPackets.group(2).trim());
        }
        return Optional.of(new PingData(startTime, transmitted, received, min, avg, max, host));
    }

    private static Integer parseToIntegerOrNull(String s) throws NullPointerException {
        try {
            double d = Double.parseDouble(s);
            return (int) Math.round(d);
        } catch (Exception e) {
            return null;
        }
    }
}
