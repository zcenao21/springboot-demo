package com.example.springbootdemo.controller;

import com.example.springbootdemo.service.ExtractService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TaskAsyncRunController {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    ExtractService extractService;

    @RequestMapping("/runTask")
    @ResponseBody
    public void runTask(@RequestParam Integer seconds) throws InterruptedException {
        extractService.extract(seconds);
    }
}
