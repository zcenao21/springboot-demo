package com.example.springbootdemo.entity;

import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * create by zhangchao29
 * dt: 20231107 10:37
 *    tempDir示例：/abc/.tmp
 *    tempFileName示例：input.csv_12
 */
public class TempDirectoryAndFile {
	private String tempDir;
	private String tempFileName;
	private Integer fileSuffix; // 临时文件末尾数字

	public TempDirectoryAndFile(String tempDir, String tempFileName){
		this.tempDir = tempDir;
		this.tempFileName = tempFileName;
	}

	public String getMergeFileName() {
		String pathReplace = tempDir.replace(".tmp", "");
		String nameReplace = tempFileName.replaceAll("_\\d+$","");
		return pathReplace + nameReplace;
	}

	public String getTempFilePrefix() {
		String nameReplace = tempFileName.replaceAll("_\\d+$","");
		return tempDir + "/" + nameReplace;
	}

	public Integer getTempFileCnt() {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(tempFileName);
		String cntStr = "1";
		if(matcher.find()){
			cntStr = matcher.group();
		}

		return Integer.parseInt(cntStr);
	}
}
