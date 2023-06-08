package com.example.springbootdemo.config;


import com.example.springbootdemo.entity.Car;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Car.class)
public class Config {
}
