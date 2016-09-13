package com.francosmith.util.excel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.francosmith.util.Config;

public class ColumnDataFilter extends DefaultDataFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(ColumnDataFilter.class);
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat(Config.getString("simple_date_format"));
	
	private int columnIndex;
	private String[] targetValues;
	
	public ColumnDataFilter(int columnIndex, String... targetValue) {
		this.columnIndex = columnIndex;
		this.targetValues = targetValue;
	}

	@Override
	public List<JSONObject> process(XSSFSheet sheet) {
		
		logger.debug("row filter with conditions: column [{}], target [{}]", this.columnIndex, Arrays.toString(targetValues));
		mapHeaderName(sheet);
		
		List<JSONObject> records = new ArrayList<JSONObject>(sheet.getLastRowNum());
		JSONObject record;
		
		Iterator<Row> rowIterator = sheet.iterator();
		String[] colNames = columnNameMap.keySet().toArray(new String[0]);
		ExcelCellHandler cellHandler = new DefaultExcelCellHandler();
		Cell cell;
		
		while(rowIterator.hasNext()) {
			Row row = rowIterator.next();
			record = new JSONObject();
			
			if (isMatchedRow(row)) {
				for (String columnKey : colNames) {
					cell = row.getCell(columnNameMap.get(columnKey), Row.CREATE_NULL_AS_BLANK);
					record.put(columnKey, cellHandler.process(cell));
				}
				
				records.add(record);
			}
		}
		
		return records;
	}

	private boolean isMatchedRow(Row row) {
		
		boolean result = false;
		
		Cell filterCell = row.getCell(columnIndex);
		
		if (filterCell == null || filterCell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return false;
		}
		
		
		
		switch (filterCell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				if (isMatched(filterCell.getStringCellValue())) {
					result = true;
				}
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(filterCell)) {
					String date = SDF.format(filterCell.getDateCellValue());
					if (isMatched(date)) {
						result = true;
					}
				} else {
					if (isMatched(String.valueOf(filterCell.getNumericCellValue()))) {
						result = true;
					}
				}
				break;
				
			default: break;
		}
		
		//logger.debug("filter row:" + row.getRowNum() + ", cell:" + filterCell + ", result:" + result);
		return result;
	}
	
	private boolean isMatched(String value) {
		
		for (String each : targetValues) {
			if (value.equals(each)) {
				return true;
			}
		}
		
		return false;
	}
}
