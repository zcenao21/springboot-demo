package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.CsvAnalysisResult;
import com.example.springbootdemo.dto.CsvMatchResult;
import com.example.springbootdemo.service.UploadService;
import com.example.springbootdemo.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @RequestMapping(value="/uploadCsvA", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadCsvA(@RequestPart MultipartFile file) {
        return uploadCsvFile(file, "A");
    }

    @RequestMapping(value="/uploadCsvB", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadCsvB(@RequestPart MultipartFile file) {
        return uploadCsvFile(file, "B");
    }

    /**
     * 通用CSV文件上传处理方法
     * @param file 上传的文件
     * @param fileType 文件类型标识 (A 或 B)
     * @return 处理结果
     */
    private Map<String, Object> uploadCsvFile(MultipartFile file, String fileType) {
        log.info("上传CSV文件{}: {}", fileType, file.getOriginalFilename());

        Map<String, Object> result = new HashMap<>();

        try {
            // 验证文件类型
            if (!isCsvFile(file)) {
                return createErrorResult("请上传CSV格式文件");
            }

            // 验证文件大小
            if (file.getSize() > ONE_HUNDRED_MB) {
                return createErrorResult("文件大小不能超过100MB");
            }

            // 解析CSV文件
            CsvAnalysisResult analysisResult = uploadService.analyzeCsvFile(file, fileType);

            // 构建成功结果
            result.put("success", true);
            result.put("message", "CSV文件" + fileType + "上传成功");
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            result.put("rowCount", analysisResult.getRowCount());
            result.put("columnCount", analysisResult.getColumnCount());
            result.put("columns", analysisResult.getColumns());
            result.put("preview", analysisResult.getPreviewData());

            log.info("CSV文件{}解析完成: 行数={}, 列数={}", fileType, analysisResult.getRowCount(), analysisResult.getColumnCount());

        } catch (Exception e) {
            log.error("上传CSV文件{}失败", fileType, e);
            return createErrorResult("文件处理失败: " + e.getMessage());
        }

        return result;
    }

    @RequestMapping(value="/api/excel/match", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> matchCsvFiles(@RequestPart MultipartFile fileA,
                                           @RequestPart MultipartFile fileB,
                                           @RequestParam String matchRules) {
        log.info("开始匹配CSV文件: {} vs {}", fileA.getOriginalFilename(), fileB.getOriginalFilename());
        log.info("匹配规则配置: {}", matchRules);

        Map<String, Object> result = new HashMap<>();

        try {
            // 验证文件
            if (!isCsvFile(fileA) || !isCsvFile(fileB)) {
                return createErrorResult("请确保上传的都是CSV格式文件");
            }

            if (fileA.getSize() > ONE_HUNDRED_MB || fileB.getSize() > ONE_HUNDRED_MB) {
                return createErrorResult("文件大小不能超过100MB");
            }

            // 解析两个CSV文件
            CsvAnalysisResult analysisA = uploadService.analyzeCsvFile(fileA, "MATCH_A");
            CsvAnalysisResult analysisB = uploadService.analyzeCsvFile(fileB, "MATCH_B");

            // 执行匹配，传入匹配规则配置
            CsvMatchResult matchResult = uploadService.matchCsvFiles(analysisA, analysisB, matchRules);

            // 构建响应结果
            result.put("success", true);
            result.put("message", "匹配完成");
            result.put("totalRecords", matchResult.getTotalRecords());
            result.put("matchedRecords", matchResult.getMatchedRecords());
            result.put("unmatchedRecords", matchResult.getUnmatchedRecords());
            result.put("matchRate", matchResult.getMatchRate());
            result.put("matchDetails", matchResult.getMatchDetails());

            // 如果有匹配结果文件，提供下载链接
            if (matchResult.getResultFilePath() != null) {
                result.put("downloadUrl", "/api/excel/download/" + matchResult.getResultFileId());
            }

            log.info("CSV文件匹配完成: 总记录数={}, 匹配数={}, 匹配率={}",
                    matchResult.getTotalRecords(), matchResult.getMatchedRecords(), matchResult.getMatchRate());

        } catch (Exception e) {
            log.error("CSV文件匹配失败", e);
            return createErrorResult("匹配处理失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 创建错误结果
     * @param message 错误信息
     * @return 错误结果Map
     */
    private Map<String, Object> createErrorResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        return result;
    }

    /**
     * 验证是否为CSV文件
     */
    private boolean isCsvFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return false;
        }

        String contentType = file.getContentType();
        return fileName.toLowerCase().endsWith(".csv") ||
                "text/csv".equals(contentType) ||
                "application/csv".equals(contentType);
    }

}
