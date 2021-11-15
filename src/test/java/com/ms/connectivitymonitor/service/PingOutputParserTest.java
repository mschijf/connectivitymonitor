package com.ms.connectivitymonitor.service;

import com.ms.connectivitymonitor.commandline.ping.PingOutputParser;
import com.ms.connectivitymonitor.entity.PingData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PingOutputParserTest {

    private static final String pingTextRaspBerry =
            "PING ziggo.nl (213.46.237.24) 56(84) bytes of data.\n" +
            "64 bytes from sales.upc-cablecom.ch (213.46.237.24): icmp_seq=1 ttl=247 time=63.8 ms\n" +
            "64 bytes from sales.upc-cablecom.ch (213.46.237.24): icmp_seq=2 ttl=247 time=16.7 ms\n" +
            "64 bytes from sales.upc-cablecom.ch (213.46.237.24): icmp_seq=3 ttl=247 time=25.9 ms\n" +
            "64 bytes from sales.upc-cablecom.ch (213.46.237.24): icmp_seq=4 ttl=247 time=32.7 ms\n" +
            "\n" +
            "--- ziggo.nl ping statistics ---\n" +
            "4 packets transmitted, 4 received, 0% packet loss, time 3005ms\n" +
            "rtt min/avg/max/mdev = 16.650/34.758/63.799/17.706 ms\n";

    private static final String pingTextMac =
            "PING ziggo.nl (213.46.237.24): 56 data bytes\n" +
            "64 bytes from 213.46.237.24: icmp_seq=0 ttl=247 time=37.742 ms\n" +
            "64 bytes from 213.46.237.24: icmp_seq=1 ttl=247 time=26.602 ms\n" +
            "64 bytes from 213.46.237.24: icmp_seq=2 ttl=247 time=11.355 ms\n" +
            "64 bytes from 213.46.237.24: icmp_seq=3 ttl=247 time=13.162 ms\n" +
            "\n" +
            "--- ziggo.nl ping statistics ---\n" +
            "4 packets transmitted, 4 packets received, 0.0% packet loss\n" +
            "round-trip min/avg/max/stddev = 11.355/22.215/37.742/10.726 ms\n";

    private static final String pingTextNoConnection =
            "PING bol.com (185.14.169.113) 56(84) bytes of data.\n" +
            "\n" +
            "--- bol.com ping statistics ---\n" +
            "4 packets transmitted, 0 received, 100% packet loss, time 3051ms\n" +
            "\n";

    private static final String pingTextNoConnection2 =
            "PING zigo.nl (136.144.229.11) 56(84) bytes of data.\n" +
            "From r2-s2.t2.rtm0.transip.net (136.144.192.135) icmp_seq=1 Time to live exceeded\n" +
            "From r2-s2.t2.rtm0.transip.net (136.144.192.135) icmp_seq=2 Time to live exceeded\n" +
            "From r2-s2.t2.rtm0.transip.net (136.144.192.135) icmp_seq=3 Time to live exceeded\n" +
            "\n" +
            "--- zigo.nl ping statistics ---\n" +
            "4 packets transmitted, 0 received, +3 errors, 100% packet loss, time 3005ms\n";

    PingOutputParser pingOutputParser;
    LocalDateTime now;

    @BeforeEach()
    void initTest() {
        pingOutputParser = new PingOutputParser();
        now = LocalDateTime.now();
    }

    @Test
    void macTest() {
        PingData dataToTest = pingOutputParser.parsePingOutput(now, "dummy", pingTextMac).orElse(new PingData());
        assertEquals(4, dataToTest.getPacketsTransmitted());
        assertEquals(4, dataToTest.getPacketsReceived());
        assertEquals(11, dataToTest.getMinTimeMillis());
        assertEquals(22, dataToTest.getAvgTimeMillis());
        assertEquals(38, dataToTest.getMaxTimeMillis());
        assertEquals("dummy", dataToTest.getHost());
        assertEquals(now, dataToTest.getRunDateTime());
    }

    @Test
    void raspberryTest() {
        PingData dataToTest = pingOutputParser.parsePingOutput(now, "dummy", pingTextRaspBerry).orElse(new PingData());
        assertEquals(4, dataToTest.getPacketsTransmitted());
        assertEquals(4, dataToTest.getPacketsReceived());
        assertEquals(17, dataToTest.getMinTimeMillis());
        assertEquals(35, dataToTest.getAvgTimeMillis());
        assertEquals(64, dataToTest.getMaxTimeMillis());
        assertEquals("dummy", dataToTest.getHost());
        assertEquals(now, dataToTest.getRunDateTime());
    }

    @Test
    void raspberryNoPacketsTest() {
        PingData dataToTest = pingOutputParser.parsePingOutput(now, "dummy", pingTextNoConnection).orElse(new PingData());
        assertEquals(4, dataToTest.getPacketsTransmitted());
        assertEquals(0, dataToTest.getPacketsReceived());
        assertEquals(0, dataToTest.getMinTimeMillis());
        assertEquals(0, dataToTest.getAvgTimeMillis());
        assertEquals(0, dataToTest.getMaxTimeMillis());
        assertEquals("dummy", dataToTest.getHost());
        assertEquals(now, dataToTest.getRunDateTime());
    }

    @Test
    void raspberryNoPacketsTest2() {
        PingData dataToTest = pingOutputParser.parsePingOutput(now, "dummy", pingTextNoConnection2).orElse(new PingData());
        assertEquals(4, dataToTest.getPacketsTransmitted());
        assertEquals(0, dataToTest.getPacketsReceived());
        assertEquals(0, dataToTest.getMinTimeMillis());
        assertEquals(0, dataToTest.getAvgTimeMillis());
        assertEquals(0, dataToTest.getMaxTimeMillis());
        assertEquals("dummy", dataToTest.getHost());
        assertEquals(now, dataToTest.getRunDateTime());
    }
}

