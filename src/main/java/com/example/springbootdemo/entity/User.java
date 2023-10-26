package com.example.springbootdemo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
	@TableField(exist = false)
	private String myContent;

	private Long id;
	private String name;
	private Integer age;
	private String email;
}
