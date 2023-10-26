package com.example.springbootdemo.config;


import com.example.springbootdemo.entity.Car;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncPoolConfig {

	@Bean(name="tagExtractAsyncExecutor")
	public AsyncTaskExecutor tagExtractAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new AsyncPoolConfigExecutor();
		executor.setThreadNamePrefix("tag-userset-async-execute");
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(6);
		executor.setQueueCapacity(2);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setWaitForTasksToCompleteOnShutdown(true);
		return executor;
	}
}
