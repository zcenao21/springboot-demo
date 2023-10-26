package com.example.springbootdemo.exception;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(value = Ordered.HIGHEST_PRECEDENCE)
//@Component
public class CustomExceptionResolver implements HandlerExceptionResolver {
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
										 Object handler, Exception ex) {
		try {
			response.sendError(521, "我的错！❌");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new ModelAndView();
	}
}
