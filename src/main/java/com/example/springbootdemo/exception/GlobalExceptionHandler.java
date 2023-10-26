package com.example.springbootdemo.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
//	@ExceptionHandler({ArithmeticException.class})
	public String handleMyException(Exception e){
		log.info("Exception: {}", e);
		return "/";
	}
}
