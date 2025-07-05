package com.example.springbootdemo.util;

import com.example.springbootdemo.enums.MergeOption;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.springbootdemo.controller.UploadController.JUPYTER_FILE_DOMAIN;

@Slf4j
public class HttpUtil {
	private static Long FIVE_MINUTES = 5 * 60* 1000L;
	private static int CHUNK_SIZE_20M = 20 * 1000 * 1000;

	public static String sendFilePost(String url, InputStream inputStream, Map<String, String> params,
									  Map<String, String> headerParam) {
		log.info("sendFilePost called, param:{}", params.toString());
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String result = "";

		try {
			HttpPost httpPost = new HttpPost(url);
			if (MapUtils.isNotEmpty(headerParam)) {
				Iterator var7 = headerParam.keySet().iterator();

				while (var7.hasNext()) {
					String key = (String) var7.next();
					httpPost.setHeader(key, (String) headerParam.get(key));
				}
			}

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setCharset(Charset.forName("utf-8"));
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addBinaryBody("file", inputStream, ContentType.MULTIPART_FORM_DATA, (String) params.get("name"));
			Iterator var25 = params.entrySet().iterator();

			while (var25.hasNext()) {
				Map.Entry<String, String> e = (Map.Entry) var25.next();
				builder.addTextBody((String) e.getKey(), (String) e.getValue());
			}

			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
			}
		} catch (IOException var21) {
			log.error("sendFilePost_error", var21);
		} catch (Exception var22) {
			log.error("sendFilePost_error", var22);
		} finally {
			try {
				httpClient.close();
			} catch (IOException var20) {
				log.error("sendFilePost_error", var20);
			}
		}

		return result;
	}

	public static String getTmpDirName(){
		String uuid = UuidUtil.getUUid();
		String timeStr = DateUtil.getCurrTimeStr("yyyy-MM-dd_HH-mm-ss");

		return "./_tmp_" + timeStr + "_" + uuid;
	}

	public static String sendLargeFilePost(String url, InputStream inputStream, Map<String, String> params,
									  Map<String, String> headerParam) throws FileNotFoundException, InterruptedException {
		// 首先得到临时文件目录，_tmp_${当前时间yyyy-MM-dd_HH-mm-ss格式}_${uuid}
		String tmpFileDir = getTmpDirName();

		// 将文件拆分到临时目录
		int splitCnt = splitFile2TmpDir(tmpFileDir, inputStream);
		if(splitCnt<0){
			throw new RuntimeException("拆分文件失败！");
		}

		// 传输拆分文件
		sendLargeFilePostInner(tmpFileDir, splitCnt, url, params, headerParam);

		// 删除临时目录
		try{
			FileUtils.deleteDirectory(new File(tmpFileDir));
		} catch (IOException e) {
			log.error("delete dir {} error!", tmpFileDir);
		}
		return "";
	}

	private static void sendLargeFilePostInner(String tmpFileDir, int splitCnt,  String url,
											   Map<String, String> params, Map<String, String> headerParam) throws FileNotFoundException, InterruptedException {
		for(int i = 1; i <= splitCnt; i++) {
			if (i == splitCnt) {
				params.put("merge", "true"); // 最后一个文件添加合并标志
			}
			Map<String, String> paramsNew = new HashMap<>();
			String pathProcessed = params.getOrDefault("path", "/tmp") + "/.tmp";
			paramsNew.put("path", pathProcessed);
			String fileNamePart = params.getOrDefault("name", "file") + "_"+i;
			paramsNew.put("name", fileNamePart);
			paramsNew.put("merge", "true"); // 最后一个文件添加合并标志
			if(i==splitCnt){
				paramsNew.put("merge", MergeOption.MERGE.toString()); // 最后一个文件添加合并标志
			}

			log.info("now upload file {}", tmpFileDir + "/chunk_" + i);
			sendFilePost(url, new FileInputStream(tmpFileDir + "/chunk_" + i), paramsNew, headerParam);
		}
	}

	/**
	 * 将stream分成多份文件
	 * @param tmpFileDir
	 * @param inputStream
	 * @return 返回-1表示有错误
	 */
	private static int splitFile2TmpDir(String tmpFileDir, InputStream inputStream) {
		int chunkIndex = 0;
		byte[] chunk = new byte[CHUNK_SIZE_20M];
		int bytesRead;
		FileUtil.checkOrCreateDir(tmpFileDir);
		try{
			while ((bytesRead = inputStream.read(chunk)) != -1) {
				File chunkFile = new File(tmpFileDir + "/chunk_"+ ++chunkIndex);
				OutputStream outputStream = new FileOutputStream(chunkFile);
				outputStream.write(chunk, 0, bytesRead);
			}
		}catch (IOException e){
			log.error("sendLargeFilePost upload error:{}", e);
			chunkIndex = -1;
		}
		return chunkIndex;
	}
}
