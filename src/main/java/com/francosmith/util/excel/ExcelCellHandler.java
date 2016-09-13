package com.francosmith.util.excel;

import org.apache.poi.ss.usermodel.Cell;

public interface ExcelCellHandler {

	Object process(Cell cell);

}
