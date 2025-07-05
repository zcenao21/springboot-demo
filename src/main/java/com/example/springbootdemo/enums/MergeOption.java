package com.example.springbootdemo.enums;

public enum MergeOption {
	MERGE("MERGE"), // 合并所有临时文件
	PART("PART"), // 只是部分文件，不合并
	NONE("NONE"); // 单个文件，不合并

	private String mergeOp;

	MergeOption(String mergeOp){
		this.mergeOp = mergeOp;
	}
}
