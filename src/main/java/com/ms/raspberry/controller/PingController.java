package com.ms.raspberry.controller;

import com.ms.raspberry.entity.PingData;
import com.ms.raspberry.service.PingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RequestMapping(value = "/v1")
@RestController
@Api(tags = {"PingController van Martin"}, description = "Omschrijving")
public class PingController {

    PingService pingService;

    @Autowired
    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    private static final Logger log = LoggerFactory.getLogger(PingController.class);

    @PostMapping("/ping/")
    @ApiOperation(value = "The do speed test method", notes = "notes at this method")
    public ResponseEntity<PingData> doPing() {
        Optional<PingData> data = pingService.doPing();
        return data.isPresent() ? ResponseEntity.ok(data.get()) : ResponseEntity.noContent().build();
    }

    @GetMapping("/ping/{id}")
    @ApiOperation(value = "Fetch Ping record", notes = "get Ping data")
    public ResponseEntity<PingData> getPingRecord(@PathVariable(name="id") Integer id) {
        Optional<PingData> data = pingService.getPingData(id);
        return data.isPresent() ? ResponseEntity.ok(data.get()) : ResponseEntity.notFound().build();
    }


}