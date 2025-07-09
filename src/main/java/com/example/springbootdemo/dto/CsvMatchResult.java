package com.example.springbootdemo.dto;

import lombok.Data;
import java.util.List;

@Data
public class CsvMatchResult {
    private int totalRecords;
    private int matchedRecords;
    private int unmatchedRecords;
    private String matchRate;
    private List<MatchDetail> matchDetails;
    private String resultFilePath;
    private String resultFileId;
    
    @Data
    public static class MatchDetail {
        private String dataA;
        private String dataB;
        private boolean matched;
        private String matchField;
        private double similarity;
    }
}