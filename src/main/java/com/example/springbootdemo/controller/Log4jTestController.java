package com.example.springbootdemo.controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class Log4jTestController {

    private static final Logger logger = LogManager.getLogger(Log4jTestController.class);

    @RequestMapping(value = "hello")
    public String Hello(){
        String str = "Try ${date:YY-mm-dd}";
        logger.error(str);
        return "hello";
    }
}