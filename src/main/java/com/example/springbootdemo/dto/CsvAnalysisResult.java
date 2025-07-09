package com.example.springbootdemo.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CsvAnalysisResult {
    private int rowCount;
    private int columnCount;
    private List<CsvColumn> columns;
    private List<Map<String, Object>> previewData;
    private String fileId;
    
    @Data
    public static class CsvColumn {
        private String name;
        private String type;
        private int index;
        private boolean nullable;
        private Object sampleValue;
    }
}