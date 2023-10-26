package com.example.springbootdemo.controller;

import com.example.springbootdemo.entity.InitialRemark;
import com.example.springbootdemo.service.RemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
public class RemarkController {
	@Autowired
	RemarkService remarkService;

	@ResponseBody
	@RequestMapping("/remarkCount")
	public String getCount(){
		return remarkService.getCount().toString();
	}

	@ResponseBody
	@RequestMapping("/topRemarks")
	public String getTopRemarks(){
		StringBuilder sb = new StringBuilder();
		for(InitialRemark remark : remarkService.topRemark()){
			sb.append(remark.toString());
			sb.append("|");
		}
		return sb.toString();
	}
}
