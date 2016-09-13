package com.francosmith.util.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.json.JSONObject;

public class DefaultDataFilter implements DataFilter {
	
	protected final Map<String, Integer> columnNameMap = new HashMap<String, Integer>();
	
	public List<JSONObject> process(XSSFSheet sheet) {
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
			
			for (String columnKey : colNames) {
				cell = row.getCell(columnNameMap.get(columnKey), Row.CREATE_NULL_AS_BLANK);
				System.out.println("cell:"+ cell);
				record.put(columnKey, cellHandler.process(cell));
			}
			
			records.add(record);
		}
		
		return records;
	}
	
	protected void mapHeaderName(XSSFSheet sheet) {
		int colNum = sheet.getRow(0).getLastCellNum();
		Cell cell = null;
		
		if (sheet.getRow(0).cellIterator().hasNext()) {
			for (int i=0; i < colNum; i++) {
				cell = sheet.getRow(0).getCell(i);
				if (cell != null) {
					columnNameMap.put(sheet.getRow(0).getCell(i).getStringCellValue(), i);
				}
			}
		}
	}//:
}
