package com.example.springbootdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class InitialRemark {
	int id;
	int idInput;
	int remarkStatus;
	String content;
	int userCount;
	String regexMatch;
	String regexUnmatch;
}
