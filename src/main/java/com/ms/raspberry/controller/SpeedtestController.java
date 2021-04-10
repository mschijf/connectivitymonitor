package com.ms.raspberry.controller;

import com.ms.raspberry.entity.SpeedTestData;
import com.ms.raspberry.service.SpeedTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RequestMapping(value = "/v1")
@RestController
@Api(tags = {"SpeedtestController van Martin"})
public class SpeedtestController {

    private final SpeedTestService speedTestService;

    @Autowired
    public SpeedtestController(SpeedTestService speedTestService) {
        this.speedTestService = speedTestService;
    }

    @PostMapping("/speedtest/")
    @ApiOperation(value = "The do speed test method", notes = "notes at this method")
    public ResponseEntity<SpeedTestData> doSpeedTest() {
        Optional<SpeedTestData> data = speedTestService.doSpeedTest();
        return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/speedtest/{id}")
    @ApiOperation(value = "Fetch speedtest record", notes = "get speedtest data")
    public ResponseEntity<SpeedTestData> getSpeedTestRecord(@PathVariable(name="id") Integer id) {
        Optional<SpeedTestData> data = speedTestService.getSpeedTestData(id);
        return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}