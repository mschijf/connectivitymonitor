package com.ms.connectivitymonitor.controller;

import com.ms.connectivitymonitor.entity.PingData;
import com.ms.connectivitymonitor.service.PingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;


@RequestMapping(value = "/v1")
@RestController
@Api(tags = {"PingController van Martin"})
public class PingController {

    PingService pingService;

    @Autowired
    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @PostMapping("/ping/")
    @ApiOperation(value = "The do speed test method", notes = "notes at this method")
    public ResponseEntity<PingData> doPing() {
        Optional<PingData> data = pingService.doPing();
        return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}