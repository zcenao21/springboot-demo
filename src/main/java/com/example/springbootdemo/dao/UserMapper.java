package com.example.springbootdemo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootdemo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
