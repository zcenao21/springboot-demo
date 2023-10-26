package com.example.springbootdemo.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class AsyncPoolConfigExecutor extends ThreadPoolTaskExecutor{

	public void showThreadPoolInfo() { // 打印线程池的状态日志
		ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();
		log.info("taskCount [{}], completedTaskCount [{}], activeCount [{}], queueSize [{}]",
				threadPoolExecutor.getTaskCount(), threadPoolExecutor.getCompletedTaskCount(),
				threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size());
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) { // 重写submit方法，实际上什么也没有干，但是必须重写，否则无法实现面向切面编程
		return super.submit(task);
	}
}
