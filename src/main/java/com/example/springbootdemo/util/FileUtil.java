package com.example.springbootdemo.util;

import java.io.File;
import java.util.Arrays;

public class FileUtil {
	public static boolean checkOrCreateDir(String dir) {
		System.out.println("开始创建临时文件"+dir);
		File file = new File(dir);
		if (!file.exists() && !file.isDirectory()) {
			System.out.println("真正开始创建临时文件");
			file.mkdirs();
		}
		System.out.println("结束创建临时文件");
		return true;
	}
}
