package com.ms.mock.commandline.ookla;

import com.ms.raspberry.commandline.ookla.OoklaJsonParser;
import com.ms.raspberry.commandline.ookla.OoklaSpeedTestExecutor;
import com.ms.raspberry.entity.SpeedTestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Profile("default")
@Component
public class OoklaSpeedTestExecutorMock implements OoklaSpeedTestExecutor {
    private OoklaJsonParser ooklaOutputParser;

    @Autowired
    public OoklaSpeedTestExecutorMock(OoklaJsonParser ooklaOutputParser) {
        this.ooklaOutputParser = ooklaOutputParser;
    }

    public Optional<SpeedTestData> execute() {
        return ooklaOutputParser.ooklaOutputToSpeedTestData(mockOutput);
    }

    private String mockOutput =
            "{\n" +
            "  \"download\": {\n" +
            "    \"bandwidth\": 31842214, \n" +
            "    \"bytes\": 291688576, \n" +
            "    \"elapsed\": 9314\n" +
            "  }, \n" +
            "  \"interface\": {\n" +
            "    \"externalIp\": \"84.105.4.111\", \n" +
            "    \"internalIp\": \"192.168.178.59\", \n" +
            "    \"isVpn\": false, \n" +
            "    \"macAddr\": \"DC:A6:32:64:11:D2\", \n" +
            "    \"name\": \"eth0\"\n" +
            "  }, \n" +
            "  \"isp\": \"Ziggo\", \n" +
            "  \"packetLoss\": 0, \n" +
            "  \"ping\": {\n" +
            "    \"jitter\": 1.129, \n" +
            "    \"latency\": 9.646\n" +
            "  }, \n" +
            "  \"result\": {\n" +
            "    \"id\": \"d0577651-7949-4c4c-8dac-3765ba89f20c\", \n" +
            "    \"url\": \"https://www.speedtest.net/result/c/d0577651-7949-4c4c-8dac-3765ba89f20c\"\n" +
            "  }, \n" +
            "  \"server\": {\n" +
            "    \"country\": \"Netherlands\", \n" +
            "    \"host\": \"speedtest.claranet.nl\", \n" +
            "    \"id\": 30847, \n" +
            "    \"ip\": \"212.61.188.174\", \n" +
            "    \"location\": \"Amsterdam\", \n" +
            "    \"name\": \"Claranet Benelux B.V.\", \n" +
            "    \"port\": 8080\n" +
            "  }, \n" +
            "  \"timestamp\": \"2021-03-25T19:58:14Z\", \n" +
            "  \"type\": \"result\", \n" +
            "  \"upload\": {\n" +
            "    \"bandwidth\": 3200280, \n" +
            "    \"bytes\": 13167616, \n" +
            "    \"elapsed\": 4112\n" +
            "  }\n" +
            "}\n";
}