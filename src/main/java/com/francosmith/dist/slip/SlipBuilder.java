package com.francosmith.dist.slip;

import com.francosmith.util.excel.DataFilter;

public interface SlipBuilder {
	
	public void build(String dest, String excelFile, int sheetNo, DataFilter filter);
}
