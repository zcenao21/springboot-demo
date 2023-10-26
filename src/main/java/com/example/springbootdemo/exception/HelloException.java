package com.example.springbootdemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "别用这种方式打招呼！")
public class HelloException extends RuntimeException{
	public HelloException(){}

	public HelloException(String message){
		super(message);
	}
}
