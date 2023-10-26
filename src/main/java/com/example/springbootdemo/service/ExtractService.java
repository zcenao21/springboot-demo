package com.example.springbootdemo.service;

import com.example.springbootdemo.dao.InitialRemarkMapper;
import com.example.springbootdemo.entity.InitialRemark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtractService {
	@Async("tagExtractAsyncExecutor")
	public void extract(Integer seconds) throws InterruptedException {
		System.out.println(Thread.currentThread().toString()+" sleep " + seconds + "seconds");
		long seconds_sleep = seconds * 1000;
		Thread.sleep(seconds_sleep);
		System.out.println(Thread.currentThread().toString()+" has slept for " + seconds + "seconds, now finished");
	}
}
