package com.example.springbootdemo.service;

import com.example.springbootdemo.entity.TempDirectoryAndFile;
import com.example.springbootdemo.enums.MergeOption;
import com.example.springbootdemo.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
@Slf4j
public class UploadService {
	/**
	 * 文件上传
	 * @param name
	 * @param path
	 * @param merge merge表示最后一个文件了合并
	 *              part表示还在传输，当前只是一个部分的文件
	 *              none表示直接放到对应文件夹即可，不需要合并文件
	 * @param file
	 * @throws Exception
	 */
	public void uploadFile(String name,String path, String merge, MultipartFile file) throws Exception{
		InputStream input = null;
		OutputStream output = null;
		FileUtil.checkOrCreateDir(path);
		try {
			input = file.getInputStream();
			output = new FileOutputStream(path+"/"+name);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) != -1) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}

		// 若为拆分的大文件的最后一个文件，则参数merge为MergeOption.MERGE，就开始最后一步的合并了
		if (merge.equals(MergeOption.MERGE.toString())) {
			mergeTempFiles(path, name);
		}
	}

	/**
	 * 将临时文件夹中的临时文件全部合并，并删除临时文件夹
	 * @param path 临时文件夹：类似/xxx/xxx/.tmp
	 * @param name 临时文件，为大文件拆分后的文件: 类似 xxxx_12, 表示第12个文件，拆分后缀从1开始
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void mergeTempFiles(String path, String name) throws IOException {
		TempDirectoryAndFile tempDirectoryAndFile = new TempDirectoryAndFile(path, name);
		String mergeFileName = tempDirectoryAndFile.getMergeFileName(); // 合并后的文件名
		String tempFilesPrefix = tempDirectoryAndFile.getTempFilePrefix(); // 合并前临时文件前缀
		Integer tempFileCnt = tempDirectoryAndFile.getTempFileCnt(); // 从最终的文件名得到临时文件个数

		FileOutputStream targeFileStream = new FileOutputStream(mergeFileName);
		try {
			for (int i = 1; i <= tempFileCnt; i++) {
				log.info("now merge file {}", tempFilesPrefix + "_" + i);
				FileInputStream tempFileStream = new FileInputStream(tempFilesPrefix + "_" + i);
				try {
					byte[] buf = new byte[1024];
					int bytesRead;
					while ((bytesRead = tempFileStream.read(buf)) != -1) {
						targeFileStream.write(buf, 0, bytesRead);
					}
				} finally {
					tempFileStream.close();
				}
			}
		} finally {
			targeFileStream.close();
		}

		// 最后删除临时文件夹
		FileUtils.deleteDirectory(new File(path));
	}
}
