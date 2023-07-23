package com.example.springbootdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Slf4j
@Controller
public class UploadController {
    private static final Logger logger = LogManager.getLogger();

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

}
