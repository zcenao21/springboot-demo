package com.example.springbootdemo.service;

import com.example.springbootdemo.dao.InitialRemarkMapper;
import com.example.springbootdemo.entity.InitialRemark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemarkService {
	@Autowired
	InitialRemarkMapper initialRemarkMapper;

	public Integer getCount(){
		return initialRemarkMapper.getCount();
	}

	public List<InitialRemark> topRemark(){
		return initialRemarkMapper.topRemark();
	}
}
