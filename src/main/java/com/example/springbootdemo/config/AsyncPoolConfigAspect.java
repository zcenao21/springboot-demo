package com.example.springbootdemo.config;


import com.example.springbootdemo.SpringbootdemoApplication;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AsyncPoolConfigAspect {

	@After("execution(* com.example.springbootdemo.config.AsyncPoolConfigExecutor.submit(..))")
	public void showThreadPoolInfo() {
		AsyncPoolConfigExecutor myExecutor =
				(AsyncPoolConfigExecutor) SpringbootdemoApplication.applicationContext.getBean("tagExtractAsyncExecutor"); // 获取到线程池的Bean对象
		myExecutor.showThreadPoolInfo();
	}
}
