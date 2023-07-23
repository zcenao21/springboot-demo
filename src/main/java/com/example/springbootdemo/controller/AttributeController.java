package com.example.springbootdemo.controller;

import com.example.springbootdemo.entity.Car;
import com.example.springbootdemo.entity.Message;
import com.example.springbootdemo.entity.Person;
import com.example.springbootdemo.service.HelloService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/attribute")
public class AttributeController {
    private static final Logger logger = LogManager.getLogger();

    @GetMapping("/forward")
    public String forward(HttpServletRequest requet){
        requet.setAttribute("user", "zhangsan");
        return "forward:/attribute/info";
    }

    @GetMapping("/info")
    @ResponseBody
    public Map info(HttpServletRequest requet){
        Map<String, Object> map = new HashMap<>();
        map.put("user", requet.getAttribute("user"));
        return map;
    }

    @GetMapping("/map")
    @ResponseBody
    public void map(Map<String, Object> inputMap){
        System.out.println("hello!");
    }
}
