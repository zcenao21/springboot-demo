package com.example.springbootdemo.controller;

import com.example.springbootdemo.service.HelloService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/")
public class HelloController {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private HelloService helloService;

    @RequestMapping("/hello2")
    @ResponseBody
    public String helloWorld(){
        return helloService.sayHello();
    }

    public static void main(String[] args) {
        String name = "${java:vm}";
        logger.info("info:{}", name);
    }
}
