package com.example.springbootdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Slf4j
public class FileController {

    @PostMapping(value="/upload")
    public String uploadFile(@RequestPart("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        String saveFileName = java.util.UUID.randomUUID().toString().replace("-", "");
        String saveTempFileName = saveFileName.concat("temp");

        log.info("start saving the uploaded file {} to {}", originalFilename, saveFileName);

        try {
            //将文件上传
            //byte[] bytes = file.getBytes();
            String line;
            String uploadPath = "/Users/will/Downloads/input.txt";
            String uploadTempPath = "/Users/will/Downloads/" + saveTempFileName;

            //Files.write(Paths.get(uploadPath), bytes);
            //生成暂存文件
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(uploadPath)));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(uploadTempPath)));
            checkFile(uploadPath);
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if(firstLine&&line.startsWith("\uFEFF")){
                    line = line.replace("\uFEFF", "");
                }
                writer.write(line.trim() + "\n");
            }
            log.info("Done add uuid to temp file {}", saveTempFileName);
            writer.close();
            reader.close();
        } catch (Exception e) {
            log.error("上传uuid:{}失败，error:", saveFileName, e);
            e.printStackTrace();
        }

        return saveFileName;
    }

    public void checkFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("文件不存在,未上传成功......");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        int lineCount = 0;
        String line = null;
        while ((line = reader.readLine()) != null) {
            lineCount++;
            if (!Pattern.matches("\\d*", line)) {
                log.error("第 {} 行的数据 {} 格式不正确!!!", lineCount, line);
                throw new RuntimeException(String.format("第 %s 行的数据 %s 格式不正确!!!", lineCount, line));
            }
        }
        if (lineCount == 0) {
            log.error("该上传文件为空,或上传失败");
            throw new RuntimeException("错误！");
        }
        reader.close();
    }

    public void checkFileNew(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("文件不存在,未上传成功......");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        int lineCount = 0;
        String line = null;
        while ((line = reader.readLine()) != null) {
            lineCount++;
        }
        if (lineCount == 0) {
            log.error("该上传文件为空,或上传失败");
            throw new RuntimeException("错误！");
        }
        reader.close();
    }
}
