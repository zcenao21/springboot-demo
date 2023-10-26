package com.example.springbootdemo.mybatis_plus;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.example.springbootdemo.dao.UserMapper;
import com.example.springbootdemo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

//@SpringBootTest
public class MybatisPlusTest {
	@Autowired
	UserMapper userMapper;

	@Test
	public void test(){
		List<User> userList = userMapper.selectList(null);
		Assert.isTrue(5 == userList.size(), "");
		userList.forEach(System.out::println);
	}
}
