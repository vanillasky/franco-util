package com.francosmith.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.francosmith.dist.slip.OrderExctrator;

public class ExcelReader {
	final Logger logger = LoggerFactory.getLogger(OrderExctrator.class);
	
	
	private final DataFilter defaultDataFilter = new DefaultDataFilter();
	
	public static final int LAST_SHEET = -1;

	public JSONObject read(String filePath, int sheetIndex) throws FileNotFoundException {
		return read(filePath, sheetIndex, defaultDataFilter);
	}//:
	
	private XSSFSheet findTargetSheet(XSSFWorkbook workbook, int sheetIndex) {
		int targetSheetIndex = (sheetIndex < 0) ? workbook.getNumberOfSheets()-1 : sheetIndex;
		return workbook.getSheetAt(targetSheetIndex);
	}
	
	private JSONObject read(String filePath, int sheetIndex, DataFilter filter) throws FileNotFoundException {
		FileInputStream file = new FileInputStream(new File(filePath));
		JSONObject result = new JSONObject();
		
		List<JSONObject> records = null;
		XSSFWorkbook workbook = null;
		
		try {
			workbook = new XSSFWorkbook (file);
			XSSFSheet sheet = findTargetSheet(workbook, sheetIndex);
			
			records = filter.process(sheet);
								
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalStateException(e.getMessage(), e.getCause());
			
		} finally {
			try { if (workbook != null) workbook.close(); } catch (IOException e1) { e1.printStackTrace(); }
			try { if (file != null) file.close(); } catch (IOException e) {	logger.error("File Closing Error:", filePath); e.printStackTrace(); }
		}
		
		result.put("result", records);
		
		return result;
	}
	
	public JSONObject filter(String filePath, int sheetIndex, DataFilter dataFilter) throws FileNotFoundException {
		return read(filePath, sheetIndex, dataFilter);
	}
}//:~
