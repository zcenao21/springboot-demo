package com.example.springbootdemo.controller;

import com.example.springbootdemo.service.UploadService;
import com.example.springbootdemo.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
public class UploadController {
    private static final Logger logger = LogManager.getLogger();
    public static final String JUPYTER_FILE_DOMAIN = "http://jupyterfile.mifi.pt.xiaomi.com";
    public static  final Long ONE_HUNDRED_MB = 100 * 1000 * 1000L;

    @Autowired
    UploadService uploadService;

    @RequestMapping(value="/uploadMulti", method = RequestMethod.POST)
    public String getUser(@RequestPart MultipartFile header,
                          @RequestPart MultipartFile[] dailyPhotos) throws IOException {

        log.info("上传文件成功!头像文件大小:{}, 生活照个数:{}", header.getSize(), dailyPhotos.length);
        String headerFileName = header.getOriginalFilename();
        header.transferTo(new File("/tmp/springboot/"+headerFileName));

        for(MultipartFile photo : dailyPhotos){
            String photoName = photo.getOriginalFilename();
            photo.transferTo(new File("/tmp/springboot/"+photoName));
        }

        return "main";
    }

    @GetMapping(value="/uploadPage")
    public String goUpload(){
        return "upload";
    }

    @GetMapping(value="/uploadJoinFile")
    public String sss(@RequestParam("file")String fileName) throws FileNotFoundException, InterruptedException {
        log.info("uploadJoinFile access");
        String filePath = "/Users/will/Desktop/"+fileName;
        Map<String, String> param = new HashMap<>();
        param.put("path", "/Users/will/tmpdir");
        param.put("name", "input.csv");
        HttpUtil.sendLargeFilePost("http://localhost:2121/fileupload",new FileInputStream(filePath),
                param
        ,generateJupyterRequestHeadMap());
        log.info("uploadJoinFile end");
        return "main";
    }

    @RequestMapping("fileupload")
    public String fileUpload(@RequestParam(name = "path") String path,
                             @RequestParam(name = "name") String name,
                             @RequestParam(name = "merge") String merge,
                             @RequestPart(name = "file") MultipartFile file) throws Exception {
        log.info("fileupload:param={}", name, path);
        uploadService.uploadFile(name, path, merge, file);
        return "main";
    }

    private HashMap<String, String> generateJupyterRequestHeadMap() {
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put("isJointPlatform", "b23911728f098c408aae4b6349d31750");
        return headMap;
    }

}
