package com.francosmith.util.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.json.JSONObject;

public class DefaultExcelCellHandler implements ExcelCellHandler {

	public Object process(Cell cell) {
		
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return cell.getDateCellValue();
				} else {
					return cell.getNumericCellValue();
				}
			case Cell.CELL_TYPE_FORMULA:
				return cell.getCachedFormulaResultType();
			default: 
				return JSONObject.NULL;
		}
	}
}
