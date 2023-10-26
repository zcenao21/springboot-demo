package com.example.springbootdemo.dao;

import com.example.springbootdemo.entity.InitialRemark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InitialRemarkMapper {

	@Select("select count(1) from initial_remark;")
	public Integer getCount();

	@Select("select * from initial_remark order by id desc limit 10")
	public List<InitialRemark> topRemark();
}
