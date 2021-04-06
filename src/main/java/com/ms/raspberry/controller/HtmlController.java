package com.ms.raspberry.controller;

import com.ms.raspberry.view.HtmlPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping(value = "/")
@RestController
public class HtmlController {

    @Autowired
    private HtmlPageService htmlPageService;

    @GetMapping("/html")
    public String getSpeedTestRecord() {
        return htmlPageService.getPage();
    }


}