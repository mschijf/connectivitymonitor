package com.ms.raspberry.service;

import com.ms.raspberry.commandline.ookla.OoklaJsonParser;
import com.ms.raspberry.entity.SpeedTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OoklaJsonParserTest {

    private SpeedTestData dataToTest;

    private static final String jsonString =
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

    @BeforeEach()
    void initTest()  {
        OoklaJsonParser ooklaOutputParser = new OoklaJsonParser();
        dataToTest = ooklaOutputParser.ooklaOutputToSpeedTestData(jsonString).orElse(null);
    }

    @Test
    void latencyTest() {
        assertEquals(dataToTest.getLatencyMillis(), 9.646);
    }

    @Test
    void jitterTest() {
        assertEquals(dataToTest.getJitterMillis(), 1.129);
    }

    @Test
    void downloadTest() {
        assertEquals(dataToTest.getDownloadSpeedBytes(), 31842214);
    }

    @Test
    void uploadTest() {
        assertEquals(dataToTest.getUploadSpeedBytes(), 3200280);
    }

    @Test
    void packetLossTest() {
        assertEquals(dataToTest.getPacketLoss(), 0.0);
    }

}

