package com.example.springbootdemo.controller;

import com.example.springbootdemo.entity.Car;
import com.example.springbootdemo.entity.Message;
import com.example.springbootdemo.entity.Person;
import com.example.springbootdemo.exception.HelloException;
import com.will.service.HelloService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class HelloController {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    HelloService helloService;

    @Autowired
    Message message;

    @Autowired
    Car car;

    @Autowired
    Person person;

    @RequestMapping("/hello2")
    @ResponseBody
    public String helloWorld(){
        logger.info("hello method called!");
        int i = 1/0;
        return message.getMsg();
    }

    @RequestMapping("/hello3")
    @ResponseBody
    public String helloWorld3(){
        logger.info("hello method3 called!");
        if(true){
            throw new HelloException();
        }
        return message.getMsg();
    }

    @RequestMapping("/hello4")
    @ResponseBody
    public String helloWorld4(@RequestParam Integer a){
        logger.info("hello method4 called!");
        return message.getMsg();
    }


    @RequestMapping("/hello8")
    @ResponseBody
    public String hello3(){
        return car.toString();
    }

    @RequestMapping("/yaml")
    @ResponseBody
    public String yaml(){
        return person.toString();
    }

    @GetMapping("/param/{id}/{name}")
    public Map<String, Object> sendRequest(
            @PathVariable("id") String id,
            @PathVariable Map<String, String> allPathParams,
            @RequestHeader("Cookie") String cookieStr,
            @RequestHeader Map<String, String> headers,
            @RequestParam("age") Integer age,
            @RequestParam("interests") List<String> params,
            @CookieValue("SL_G_WPT_TO") String SL_G_WPT_TO,
            @CookieValue("SL_G_WPT_TO") Cookie cookie


                              ){
        Map<String, Object> res = new HashMap<>();
        res.put("====================>>>id", id);
        res.put("====================>>>params", allPathParams);
        res.put("====================>>>cookieStr", cookieStr);
        res.put("====================>>>headers", headers.toString());
        res.put("====================>>>age", age);
        res.put("====================>>>allParams", params);
        res.put("====================>>>SL_G_WPT_TO", SL_G_WPT_TO);
        res.put("====================>>>cookie", cookie);
        return res;
    }

    @PostMapping("/hello/save")
    @ResponseBody
    public Map saveHello(@RequestBody String body){
        Map<String, Object> map = new HashMap<>();
        map.put("body", body);
        return map;
    }

    @GetMapping("/matrix/{nameFather}/{nameSon}")
    public Map getMatrix(
            @MatrixVariable(value = "age", pathVar = "nameFather") Integer age,
            @MatrixVariable(value = "gender") List<String> gender,
            @MatrixVariable(value = "age", pathVar = "nameSon") Integer ageSon){
        Map<String, Object> map = new HashMap<>();
        map.put("ageFather", age);
        map.put("gender", Arrays.toString(gender.toArray()));
        map.put("ageSon", ageSon);
        return map;
    }

    @GetMapping("/will/hello")
    public String sayHello(){
        return helloService.sayHello("小张");
    }


    public static void main(String[] args) {
        String name = "${java:vm}";
        logger.info("info:{}", name);
    }
}
