package com.example.springbootdemo.controller;

import com.example.springbootdemo.entity.Message;
import com.example.springbootdemo.service.HelloService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/")
public class HelloController {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private HelloService helloService;
    @Autowired
    Message message;

    @RequestMapping("/hello2")
    @ResponseBody
    public String helloWorld(){
        return message.getMsg();
    }

    public static void main(String[] args) {
        String name = "${java:vm}";
        logger.info("info:{}", name);
    }
}
