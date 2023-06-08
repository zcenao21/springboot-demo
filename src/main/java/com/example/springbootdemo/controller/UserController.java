package com.example.springbootdemo.controller;

import com.example.springbootdemo.entity.Car;
import com.example.springbootdemo.entity.Message;
import com.example.springbootdemo.entity.Person;
import com.example.springbootdemo.service.HelloService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.HiddenHttpMethodFilter;


@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger();

    @RequestMapping(value="/user", method = RequestMethod.GET)
    public String getUser(){
        return "get method";
    }

    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    public String deleteUser(){
        return "delete method";
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String postUser(){
        return "post method";
    }

    //自定义filter
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
        HiddenHttpMethodFilter methodFilter = new HiddenHttpMethodFilter();
        methodFilter.setMethodParam("_m");
        return methodFilter;
    }
}
