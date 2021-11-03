package com.ms.connectivitymonitor.controller;

import com.ms.connectivitymonitor.entity.SpeedtestData;
import com.ms.connectivitymonitor.service.SpeedtestService;
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

    private final SpeedtestService speedTestService;

    @Autowired
    public SpeedtestController(SpeedtestService speedTestService) {
        this.speedTestService = speedTestService;
    }

    @PostMapping("/speedtest/")
    @ApiOperation(value = "The do speed test method", notes = "notes at this method")
    public ResponseEntity<SpeedtestData> doSpeedTest() {
        Optional<SpeedtestData> data = speedTestService.doSpeedTest();
        return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}