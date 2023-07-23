package com.example.springbootdemo.config;


import com.example.springbootdemo.entity.Car;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

@Configuration
@EnableConfigurationProperties(Car.class)
public class Config {

	@Bean
	public WebMvcConfigurer WebMvcConfigurer(){
		return new WebMvcConfigurer() {
			@Override
			public void configurePathMatch(PathMatchConfigurer configurer) {
				UrlPathHelper helper = new UrlPathHelper();
				helper.setRemoveSemicolonContent(false);
				configurer.setUrlPathHelper(helper);
			}
		};
	}
}
