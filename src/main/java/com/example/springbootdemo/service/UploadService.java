package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.CsvAnalysisResult;
import com.example.springbootdemo.dto.CsvMatchResult;
import com.example.springbootdemo.entity.TempDirectoryAndFile;
import com.example.springbootdemo.enums.MergeOption;
import com.example.springbootdemo.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UploadService {
	/**
	 * 文件上传
	 *
	 * @param name
	 * @param path
	 * @param merge merge表示最后一个文件了合并
	 *              part表示还在传输，当前只是一个部分的文件
	 *              none表示直接放到对应文件夹即可，不需要合并文件
	 * @param file
	 * @throws Exception
	 */
	public void uploadFile(String name, String path, String merge, MultipartFile file) throws Exception {
		InputStream input = null;
		OutputStream output = null;
		FileUtil.checkOrCreateDir(path);
		try {
			input = file.getInputStream();
			output = new FileOutputStream(path + "/" + name);
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
	 *
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

	// 用于缓存文件数据
	private final Map<String, Object> fileDataCache = new ConcurrentHashMap<>();

	public CsvAnalysisResult analyzeCsvFile(MultipartFile file, String fileType) throws IOException {
		CsvAnalysisResult result = new CsvAnalysisResult();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
			CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

			// 获取列信息
			Map<String, Integer> headerMap = parser.getHeaderMap();
			List<CsvAnalysisResult.CsvColumn> columns = new ArrayList<>();

			for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
				CsvAnalysisResult.CsvColumn column = new CsvAnalysisResult.CsvColumn();
				column.setName(entry.getKey());
				column.setIndex(entry.getValue());
				columns.add(column);
			}

			// 读取数据并分析类型
			List<CSVRecord> records = parser.getRecords();
			List<Map<String, Object>> previewData = new ArrayList<>();

			// 分析每列的数据类型
			analyzeColumnTypes(columns, records);

			// 生成预览数据（前10行）
			int previewCount = Math.min(10, records.size());
			for (int i = 0; i < previewCount; i++) {
				CSVRecord record = records.get(i);
				Map<String, Object> rowData = new HashMap<>();
				for (CsvAnalysisResult.CsvColumn column : columns) {
					String value = record.get(column.getIndex());
					rowData.put(column.getName(), convertValue(value, column.getType()));
				}
				previewData.add(rowData);
			}

			result.setRowCount(records.size());
			result.setColumnCount(columns.size());
			result.setColumns(columns);
			result.setPreviewData(previewData);
			result.setFileId(generateFileId(fileType));

			// 缓存文件数据供后续匹配使用
			cacheFileData(result.getFileId(), records, columns);

		} catch (Exception e) {
			log.error("解析CSV文件失败", e);
			throw new IOException("CSV文件解析失败: " + e.getMessage());
		}

		return result;
	}

	private void analyzeColumnTypes(List<CsvAnalysisResult.CsvColumn> columns, List<CSVRecord> records) {
		for (CsvAnalysisResult.CsvColumn column : columns) {
			String detectedType = detectColumnType(column.getIndex(), records);
			column.setType(detectedType);
			column.setSampleValue(getSampleValue(column.getIndex(), records));
			column.setNullable(hasNullValues(column.getIndex(), records));
		}
	}

	private String detectColumnType(int columnIndex, List<CSVRecord> records) {
		int intCount = 0;
		int doubleCount = 0;
		int dateCount = 0;
		int totalCount = 0;

		for (CSVRecord record : records) {
			if (columnIndex >= record.size()) continue;

			String value = record.get(columnIndex);
			if (value == null || value.trim().isEmpty()) continue;

			totalCount++;

			// 检查是否为整数
			if (isInteger(value)) {
				intCount++;
			}
			// 检查是否为浮点数
			else if (isDouble(value)) {
				doubleCount++;
			}
			// 检查是否为日期
			else if (isDate(value)) {
				dateCount++;
			}
		}

		if (totalCount == 0) return "STRING";

		double intRatio = (double) intCount / totalCount;
		double doubleRatio = (double) doubleCount / totalCount;
		double dateRatio = (double) dateCount / totalCount;

		// 如果80%以上的数据符合某种类型，则认为是该类型
		if (intRatio >= 0.8) return "INTEGER";
		if (doubleRatio >= 0.8) return "DOUBLE";
		if (dateRatio >= 0.8) return "DATE";

		return "STRING";
	}

	private boolean isInteger(String value) {
		try {
			Integer.parseInt(value.trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isDouble(String value) {
		try {
			Double.parseDouble(value.trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isDate(String value) {
		// 简单的日期格式检查
		String[] datePatterns = {
				"yyyy-MM-dd", "yyyy/MM/dd", "dd/MM/yyyy", "MM/dd/yyyy",
				"yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"
		};

		for (String pattern : datePatterns) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				sdf.setLenient(false);
				sdf.parse(value.trim());
				return true;
			} catch (Exception e) {
				// 继续尝试下一个格式
				log.error("parse file error,{}", e);
			}
		}
		return false;
	}

	private Object getSampleValue(int columnIndex, List<CSVRecord> records) {
		for (CSVRecord record : records) {
			if (columnIndex < record.size()) {
				String value = record.get(columnIndex);
				if (value != null && !value.trim().isEmpty()) {
					return value;
				}
			}
		}
		return null;
	}

	private boolean hasNullValues(int columnIndex, List<CSVRecord> records) {
		for (CSVRecord record : records) {
			if (columnIndex >= record.size()) return true;

			String value = record.get(columnIndex);
			if (value == null || value.trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private Object convertValue(String value, String type) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		try {
			switch (type) {
				case "INTEGER":
					return Integer.parseInt(value.trim());
				case "DOUBLE":
					return Double.parseDouble(value.trim());
				case "DATE":
					return parseDate(value.trim());
				default:
					return value;
			}
		} catch (Exception e) {
			// 转换失败时返回原始字符串
			return value;
		}
	}

	private Date parseDate(String value) {
		String[] datePatterns = {
				"yyyy-MM-dd", "yyyy/MM/dd", "dd/MM/yyyy", "MM/dd/yyyy",
				"yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"
		};

		for (String pattern : datePatterns) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				sdf.setLenient(false);
				return sdf.parse(value);
			} catch (ParseException e) {
				// 继续尝试下一个格式
			}
		}
		return null;
	}

	private String generateFileId(String fileType) {
		return fileType + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
	}

	private void cacheFileData(String fileId, List<CSVRecord> records, List<CsvAnalysisResult.CsvColumn> columns) {
		Map<String, Object> cacheData = new HashMap<>();
		cacheData.put("records", records);
		cacheData.put("columns", columns);
		cacheData.put("timestamp", System.currentTimeMillis());

		fileDataCache.put(fileId, cacheData);

		log.info("缓存文件数据: fileId={}, 记录数={}", fileId, records.size());
	}

	// 获取缓存的文件数据
	public Map<String, Object> getCachedFileData() {
		return fileDataCache;
	}

	// 清理过期缓存（可选）
	public void cleanExpiredCache() {
		long currentTime = System.currentTimeMillis();
		long expireTime = 30 * 60 * 1000; // 30分钟过期

		fileDataCache.entrySet().removeIf(entry -> {
			Map<String, Object> data = (Map<String, Object>) entry.getValue();
			Long timestamp = (Long) data.get("timestamp");
			return timestamp != null && (currentTime - timestamp) > expireTime;
		});
	}

	public CsvMatchResult matchCsvFiles(CsvAnalysisResult fileA, CsvAnalysisResult fileB, String matchRulesJson) throws IOException {
		log.info("开始匹配CSV文件数据，匹配规则: {}", matchRulesJson);

		CsvMatchResult result = new CsvMatchResult();
		List<CsvMatchResult.MatchDetail> matchDetails = new ArrayList<>();

		// 解析匹配规则
		Map<String, Object> matchRules = parseMatchRules(matchRulesJson);

		// 获取缓存的文件数据
		Map<String, Object> dataA = (Map<String, Object>)getCachedFileData().get(fileA.getFileId());
		Map<String, Object> dataB = (Map<String, Object>)getCachedFileData().get(fileB.getFileId());

		if (dataA == null || dataB == null) {
			throw new IOException("文件数据缓存不存在，请重新上传文件");
		}

		List<CSVRecord> recordsA = (List<CSVRecord>)dataA.get("records");
		List<CSVRecord> recordsB = (List<CSVRecord>) dataB.get("records");
		List<CsvAnalysisResult.CsvColumn> columnsA = (List<CsvAnalysisResult.CsvColumn>) dataA.get("columns");
		List<CsvAnalysisResult.CsvColumn> columnsB = (List<CsvAnalysisResult.CsvColumn>) dataB.get("columns");

		// 根据匹配规则执行匹配
		int matchedCount = performMatching(recordsA, recordsB, matchRules, matchDetails);
		int totalCount = Math.max(recordsA.size(), recordsB.size());

		// 设置结果
		result.setTotalRecords(totalCount);
		result.setMatchedRecords(matchedCount);
		result.setUnmatchedRecords(totalCount - matchedCount);
		result.setMatchRate(String.format("%.2f%%", (double) matchedCount / totalCount * 100));
		result.setMatchDetails(matchDetails.size() > 100 ? matchDetails.subList(0, 100) : matchDetails);

		// 生成结果文件
		String resultFileId = generateResultFile(result, fileA.getFileId(), fileB.getFileId());
		result.setResultFileId(resultFileId);

		return result;
	}

	private Map<String, Object> parseMatchRules(String matchRulesJson) {
		try {
			// 这里需要使用JSON解析库，比如Jackson或Gson
			// 临时使用简单的解析方式，实际项目中应该使用专业的JSON库
			log.info("解析匹配规则: {}", matchRulesJson);

			// 返回默认规则结构，实际应该解析JSON
			Map<String, Object> rules = new HashMap<>();
			rules.put("primaryKeys", Arrays.asList("id")); // 默认主键
			rules.put("additionalRules", new ArrayList<>()); // 附加规则
			rules.put("options", new HashMap<>()); // 匹配选项

			return rules;
		} catch (Exception e) {
			log.error("解析匹配规则失败", e);
			throw new RuntimeException("匹配规则格式错误: " + e.getMessage());
		}
	}

	private int performMatching(List<CSVRecord> recordsA, List<CSVRecord> recordsB,
							   Map<String, Object> matchRules, List<CsvMatchResult.MatchDetail> matchDetails) {

		List<String> primaryKeys = (List<String>) matchRules.get("primaryKeys");
		List<Map<String, Object>> additionalRules = (List<Map<String, Object>>) matchRules.get("additionalRules");
		Map<String, Object> options = (Map<String, Object>) matchRules.get("options");

		log.info("执行匹配 - 主键字段: {}, 附加规则数: {}", primaryKeys, additionalRules.size());

		int matchedCount = 0;

		// 为文件B创建索引以提高匹配效率
		Map<String, List<CSVRecord>> indexB = createIndex(recordsB, primaryKeys);

		// 遍历文件A的每条记录
		for (CSVRecord recordA : recordsA) {
			String keyA = buildKey(recordA, primaryKeys);
			List<CSVRecord> candidatesB = indexB.get(keyA);

			boolean foundMatch = false;

			if (candidatesB != null && !candidatesB.isEmpty()) {
				// 在候选记录中查找最佳匹配
				for (CSVRecord recordB : candidatesB) {
					if (isRecordMatch(recordA, recordB, primaryKeys, additionalRules, options)) {
						// 找到匹配
						CsvMatchResult.MatchDetail detail = createMatchDetail(recordA, recordB, primaryKeys, true);
						matchDetails.add(detail);
						matchedCount++;
						foundMatch = true;
						break; // 找到第一个匹配就停止
					}
				}
			}

			if (!foundMatch) {
				// 未找到匹配
				CsvMatchResult.MatchDetail detail = createMatchDetail(recordA, null, primaryKeys, false);
				matchDetails.add(detail);
			}
		}

		return matchedCount;
	}

	private Map<String, List<CSVRecord>> createIndex(List<CSVRecord> records, List<String> keyFields) {
		Map<String, List<CSVRecord>> index = new HashMap<>();

		for (CSVRecord record : records) {
			String key = buildKey(record, keyFields);
			index.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
		}

		return index;
	}

	private String buildKey(CSVRecord record, List<String> keyFields) {
		StringBuilder key = new StringBuilder();
		for (String field : keyFields) {
			try {
				String value = record.get(field);
				if (key.length() > 0) {
					key.append("|");
				}
				key.append(value != null ? value.trim() : "");
			} catch (Exception e) {
				// 字段不存在时使用空值
				if (key.length() > 0) {
					key.append("|");
				}
				key.append("");
			}
		}
		return key.toString();
	}

	private boolean isRecordMatch(CSVRecord recordA, CSVRecord recordB, List<String> primaryKeys,
								 List<Map<String, Object>> additionalRules, Map<String, Object> options) {

		// 首先检查主键是否匹配
		for (String key : primaryKeys) {
			try {
				String valueA = recordA.get(key);
				String valueB = recordB.get(key);

				if (!Objects.equals(valueA, valueB)) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}

		// 检查附加匹配规则
		for (Map<String, Object> rule : additionalRules) {
			if (!checkAdditionalRule(recordA, recordB, rule)) {
				return false;
			}
		}

		return true;
	}

	private boolean checkAdditionalRule(CSVRecord recordA, CSVRecord recordB, Map<String, Object> rule) {
		try {
			String fieldA = (String) rule.get("fieldA");
			String fieldB = (String) rule.get("fieldB");
			String condition = (String) rule.get("condition");

			String valueA = recordA.get(fieldA);
			String valueB = recordB.get(fieldB);

			switch (condition) {
				case "equals":
					return Objects.equals(valueA, valueB);
				case "contains":
					return valueA != null && valueB != null && valueA.contains(valueB);
				case "startsWith":
					return valueA != null && valueB != null && valueA.startsWith(valueB);
				case "endsWith":
					return valueA != null && valueB != null && valueA.endsWith(valueB);
				default:
					return Objects.equals(valueA, valueB);
			}
		} catch (Exception e) {
			log.warn("检查附加规则时出错", e);
			return false;
		}
	}

	private CsvMatchResult.MatchDetail createMatchDetail(CSVRecord recordA, CSVRecord recordB,
														List<String> keyFields, boolean matched) {
		CsvMatchResult.MatchDetail detail = new CsvMatchResult.MatchDetail();
		detail.setDataA(getRecordSummary(recordA, keyFields));
		detail.setDataB(recordB != null ? getRecordSummary(recordB, keyFields) : "无对应记录");
		detail.setMatched(matched);
		detail.setMatchField(String.join(",", keyFields));
		detail.setSimilarity(recordB != null ? calculateSimilarity(recordA, recordB, keyFields) : 0.0);

		return detail;
	}

	private String getRecordSummary(CSVRecord record, List<String> columns) {
		StringBuilder summary = new StringBuilder();
		for (String column : columns) {
			try {
				String value = record.get(column);
				if (summary.length() > 0) {
					summary.append(", ");
				}
				summary.append(column).append(": ").append(value);
			} catch (Exception e) {
				// 忽略错误
			}
		}
		return summary.toString();
	}

	private double calculateSimilarity(CSVRecord recordA, CSVRecord recordB, List<String> commonColumns) {
		int matchCount = 0;
		int totalCount = commonColumns.size();

		for (String column : commonColumns) {
			try {
				String valueA = recordA.get(column);
				String valueB = recordB.get(column);

				if (Objects.equals(valueA, valueB)) {
					matchCount++;
				}
			} catch (Exception e) {
				// 忽略错误
			}
		}

		return totalCount > 0 ? (double) matchCount / totalCount : 0.0;
	}

	private void handleRemainingRecords(List<CSVRecord> recordsA, List<CSVRecord> recordsB,
										List<String> commonColumns, List<CsvMatchResult.MatchDetail> matchDetails) {
		// 处理文件A中剩余的记录
		for (int i = recordsB.size(); i < recordsA.size(); i++) {
			CsvMatchResult.MatchDetail detail = new CsvMatchResult.MatchDetail();
			detail.setDataA(getRecordSummary(recordsA.get(i), commonColumns));
			detail.setDataB("无对应记录");
			detail.setMatched(false);
			detail.setSimilarity(0.0);
			matchDetails.add(detail);
		}

		// 处理文件B中剩余的记录
		for (int i = recordsA.size(); i < recordsB.size(); i++) {
			CsvMatchResult.MatchDetail detail = new CsvMatchResult.MatchDetail();
			detail.setDataA("无对应记录");
			detail.setDataB(getRecordSummary(recordsB.get(i), commonColumns));
			detail.setMatched(false);
			detail.setSimilarity(0.0);
			matchDetails.add(detail);
		}
	}

	private String generateResultFile(CsvMatchResult result, String fileIdA, String fileIdB) {
		// 这里可以生成CSV结果文件并保存到临时目录
		// 返回文件ID供下载使用
		return "result_" + System.currentTimeMillis();
	}

	public File getMatchResultFile(String fileId) {
		// 根据fileId获取匹配结果文件
		// 这里需要实现具体的文件获取逻辑
		return null; // 临时返回null
	}
}
